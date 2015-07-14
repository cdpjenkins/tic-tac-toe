(defproject tic-tac-toe "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/core.typed "0.2.92"]
                 [compojure "1.2.1"]
                 [hiccup "1.0.5"]
                 [ring/ring-jetty-adapter "1.1.0"]
                 [org.clojure/clojurescript "0.0-2727"]
                 [domina "1.0.3"]
                 [hiccups "0.3.0"]]
  :main ^:skip-aot tic-tac-toe.core
  :plugins [[lein-ring "0.8.12"]
            [lein-cljsbuild "1.0.4-SNAPSHOT"]]
  :ring {:handler tic-tac-toe.core/app}


  :profiles {:dev {:plugins [[com.keminglabs/cljx "0.6.0"]]}
             :uberjar {:aot :all}}
  :cljx {:builds [{:source-paths ["src-cljx"]
                 :output-path "src"
                 :rules :clj}

                {:source-paths ["src-cljx"]
                 :output-path "src-cljs"
                 :rules :cljs}]}
 :core.typed {:check [tic-tac-toe.test]}

  :target-path "target/%s"

  :cljsbuild {
    :builds [{
        ; The path to the top-level ClojureScript source directory:
        :source-paths ["src-cljs"]
        ; The standard ClojureScript compiler options:
        ; (See the ClojureScript compiler documentation for details.)
        :compiler {
          ;:preamble ["reagent/react.js"]
          :output-to "resources/public/js/cljs.js"
          :optimizations :whitespace
          :pretty-print true}}]})
