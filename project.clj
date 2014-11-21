(defproject tic-tac-toe "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [compojure "1.2.1"]
                 [hiccup "1.0.5"]
                 [ring/ring-jetty-adapter "1.1.0"]]
  :main ^:skip-aot tic-tac-toe.core
  :plugins [[lein-ring "0.8.12"]]
  :ring {:handler tic-tac-toe.core/app}

  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
