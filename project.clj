(defproject owyne "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "https://github.com/rmoehn/owyne"
  :license {:name "MIT License"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.9.0-alpha15"]
                 [cheshire "5.7.1"]]
  :profiles {:dev {:dependencies [[org.clojure/tools.namespace "0.2.11"]]
                   :source-paths ["dev"]}})
