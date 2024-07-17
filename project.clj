(defproject com.github.hden/cuid2 "0.1.0-SNAPSHOT"
  :description "Secure, collision-resistant ids optimized for horizontal scaling and performance. Next generation UUIDs."
  :url "https://github.com/hden/cuid2"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.12.0-alpha8"]]
  :plugins [[lein-cloverage "1.2.4"]]
  :repl-options {:init-ns cuid2.core}
  :profiles
  {:dev {:global-vars {*warn-on-reflection* true}}})
