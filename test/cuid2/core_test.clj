(ns cuid2.core-test
  (:require [clojure.test :refer [deftest testing is use-fixtures]]
            [cuid2.core :as core])
  (:import (java.util Random)
           (java.time Clock Instant ZoneId)))

(def context (atom core/default-context))

(defn reset-context [f]
  (let [fixed-clock (Clock/fixed Instant/EPOCH (ZoneId/of "UTC"))]
    (reset! context (merge core/default-context
                           {:clock fixed-clock
                            :counter (core/create-counter 0)
                            :fingerprint "foobar"
                            :random (Random. 0)})))
  (f))

(use-fixtures :each reset-context)

(deftest create-fingerprint-test
  (let [random (:random @context)]
    (testing "empty global fingerprint"
      (is (= "bqlb3gdcoc76b051rfps4cja5nntwrqp"
             (core/create-fingerprint {} random))))

    (testing "host fingerprint"
      (is (<= 24 (count (core/create-fingerprint)))))))

(deftest create-counter-test
  (let [counter (core/create-counter 10)]
    (is (= [11 12 13 14]
           [(counter) (counter) (counter) (counter)]))))

(deftest cuid-test
  (testing "output"
    (core/with-context @context
        (is (= "suw7wvri6o4f3kdc1f87hxqq"
               (core/cuid))))))

(deftest cuid-length-test
  (testing "default length"
    (is (= core/default-length
           (count (core/cuid)))))

  (testing "smaller length"
    (let [length 10]
      (is (= length (count (core/cuid {:length length}))))))

  (testing "larger length"
    (let [length 32]
      (is (= length (count (core/cuid {:length length})))))))

(deftest cuid?-test
  (testing "default length"
    (is (core/cuid? (core/cuid))))

  (testing "too long"
    (is (not (core/cuid? (str (core/cuid) (core/cuid) (core/cuid))))))

  (testing "empty string"
    (is (not (core/cuid? "")))))
