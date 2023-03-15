(ns cuid2.core
  (:refer-clojure :exclude [hash])
  (:import (java.security MessageDigest SecureRandom)
           (java.time Clock)
           (java.util Random)))

(def ^:const default-length 24)
(def ^:private ^:const base36-radix 36)
(def ^:private ^:const big-length 32)
(def ^:private default-clock (Clock/systemUTC))
(def ^:private ^SecureRandom default-random-source (SecureRandom.))

(defn- create-entropy
  "Create a string of random characters of the given length using the
   specified random number generator. The default length is 4 and the
   default RNG is Java's SecureRandom."
  ([]
   (create-entropy 4))
  ([length]
   (create-entropy length default-random-source))
  ([length ^Random random]
   (->> (.ints random 0 base36-radix)
        (.iterator)
        (iterator-seq)
        (take length)
        (map #(Integer/toString % base36-radix))
        (apply str))))

(defn- sha3
  "Calculate the SHA3-512 hash value of the input string and return it as a byte array."
  [^String input]
  (.digest (MessageDigest/getInstance "SHA3-512")
           (.getBytes input)))

(defn- base36
  "Returns a Base36 string representation of the given byte array."
  [input]
  (.toString (biginteger input) base36-radix))

(defn- hash
  "Compute the SHA3-512 hash of the input string."
  [^String input]
  ;; Drop the first character because it will bias the histogram
  ;; to the left.
  ;; See https://github.com/paralleldrive/cuid2/blob/v2.2.0/src/index.js#L31-L35
  (subs (base36 (sha3 input)) 1))

(def ^:private alphabets (vec (for [i (range 26)] (str (char (+ i 97))))))

(defn- random-letter
  "Return a random letter from the English alphabet."
  [^Random random]
  (let [idx (.nextInt random 26)]
    (nth alphabets idx)))

(defn create-fingerprint
  "Create a string fingerprint of the current host environment by
   hashing together a combination of system properties and random
   values. The default RNG is Java's SecureRandom."
  ([]
   (create-fingerprint {}))
  ([m]
   (create-fingerprint m (SecureRandom.)))
  ([m ^Random random]
   (let [globals (str (keys m))
         source-string (str globals (create-entropy big-length random))]
     (-> (hash source-string) (subs 0 big-length)))))

(defn create-counter
  "Return a function that generates a unique, incrementing counter value
   every time it's called."
  [init]
  (let [count (atom (bigint init))]
    #(swap! count inc)))

;; See https://github.com/paralleldrive/cuid2/blob/v2.2.0/src/index.js#L69-L71
(def ^:private ^:const initial-count-max 476782367)

(def default-context
  {:clock default-clock
   :counter (create-counter (.nextInt default-random-source initial-count-max))
   :fingerprint (create-fingerprint (System/getenv) default-random-source)
   :random default-random-source})

(def ^:dynamic ^:private *context* default-context)

(defmacro with-context
  [context & body]
  `(binding [*context* ~context]
     ~@body))

(defn- current-timestamp
  "Gets the current millisecond instant of the clock.
  The default clock is Clock/systemUTC."
  ([]
   (current-timestamp default-clock))
  ([^Clock clock]
   (.millis clock)))

(defn cuid
  "Generates a collision-resistant, distributed ID."
  ([] (cuid {}))
  ([{:keys [length] :or {length default-length}}]
   (let [{:keys [clock counter fingerprint random]} *context*
         first-letter (random-letter random)
         time (Long/toString (current-timestamp clock) base36-radix)
         count (.toString (biginteger (counter)) base36-radix)
         salt (create-entropy length random)
         hash-input (str time salt count fingerprint)]
     (str first-letter (subs (hash hash-input) 1 length)))))

(def ^:private alphanumeric #"^[0-9a-z]+$")

(defn cuid?
  ([x] (cuid? x {}))
  ([x {:keys [min max] :or {min 0 max big-length}}]
   (when x
     (try
       (and (string? x)
            (<= min (count x) max)
            (some? (re-matches alphanumeric x)))
       (catch Throwable _
         nil)))))
