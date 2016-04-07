(defproject centipair "0.1.0-SNAPSHOT"

  :description "FIXME: write description"
  :url "http://www.centipair.com"

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [selmer "1.0.0"]
                 [markdown-clj "0.9.86"]
                 [ring-middleware-format "0.7.0"]
                 [metosin/ring-http-response "0.6.5"]
                 [bouncer "1.0.0"]
                 [org.clojure/tools.logging "0.3.1"]
                 [com.taoensso/tower "3.0.2"]
                 [compojure "1.4.0"]
                 [ring/ring-defaults "0.1.5"]
                 [ring "1.4.0" :exclusions [ring/ring-jetty-adapter]]
                 [mount "0.1.10"]
                 [cprop "0.1.6"]
                 [org.clojure/tools.cli "0.3.3"]
                 [luminus-nrepl "0.1.4"]
                 [org.webjars/webjars-locator-jboss-vfs "0.1.0"]
                 [luminus-immutant "0.1.8"]
                 [org.clojure/clojurescript "1.7.228" :scope "provided"]
                 [reagent "0.5.1"]
                 [secretary "1.2.3"]
                 [org.clojure/core.async "0.2.374"]
                 [cljs-ajax "0.5.3"]
                 [luminus-log4j "0.1.3"]
                 ;;centipair dependencies
                 [crypto-random "1.2.0"];;random key generator
                 [crypto-password "0.2.0"];;password hasher
                 [danlentz/clj-uuid "0.1.6"];;uui generator
                 [org.postgresql/postgresql "9.2-1002-jdbc4"];;postgresql driver
                 [korma "0.4.2"];;sql orm
                 [com.draines/postal "1.11.3"];;email
                 [clj-time "0.11.0"];;date and time
                 [liberator "0.14.0"];;api handlers
                 [cheshire "5.5.0"];;json encoding
                 [com.novemberain/validateur "2.5.0"];;form-validation
                 [clojurewerkz/cassaforte "3.0.0-alpha1"];;cassandra
                 [org.clojure/tools.reader "1.0.0-alpha3"];;support lib for cassandra
                 ]

  :min-lein-version "2.0.0"

  :jvm-opts ["-server" "-Dconf=.lein-env"]
  :source-paths ["src/clj" "src/cljc"]
  :resource-paths ["resources"]
  :immutant {:war
             {:context-path "/"}}
  :main centipair.core

  :plugins [[lein-cprop "1.0.1"]
            [lein-cljsbuild "1.1.1"]]
  :clean-targets ^{:protect false} [:target-path [:cljsbuild :builds :app :compiler :output-dir] [:cljsbuild :builds :app :compiler :output-to]]
  :cljsbuild
  {:builds
   {:app
    {:source-paths ["src/cljc" "src/cljs"]
     :compiler
     {:output-to "resources/public/cljs/app.js"
      :output-dir "resources/public/cljs/out"
      :externs ["react/externs/react.js"]
      :pretty-print true}}}}
  
  :profiles
  {:uberjar {:omit-source true
             
              :prep-tasks ["compile" ["cljsbuild" "once"]]
              :cljsbuild
              {:builds
               {:app
                {:source-paths ["env/prod/cljs"]
                 :compiler
                 {:optimizations :advanced
                  :pretty-print false
                  :closure-warnings
                  {:externs-validation :off :non-standard-jsdoc :off}}}}} 
             
             :aot :all
             :uberjar-name "centipair.jar"
             :source-paths ["env/prod/clj"]
             :resource-paths ["env/prod/resources"]}
   :dev           [:project/dev :profiles/dev]
   :test          [:project/test :profiles/test]
   :project/dev  {:dependencies [[prone "1.0.2"]
                                 [ring/ring-mock "0.3.0"]
                                 [ring/ring-devel "1.4.0"]
                                 [pjstadig/humane-test-output "0.7.1"]
                                 [lein-figwheel "0.5.0-6"]
                                 [lein-doo "0.1.6"]
                                 [com.cemerick/piggieback "0.2.2-SNAPSHOT"]]
                  :plugins [[lein-figwheel "0.5.0-6"] [lein-doo "0.1.6"] [org.clojure/clojurescript "1.7.228"]]
                   :cljsbuild
                   {:builds
                    {:app
                     {:source-paths ["env/dev/cljs"]
                      :compiler
                      {:main "centipair.app"
                       :asset-path "/cljs/out"
                       :optimizations :none
                       :source-map true}}
                     :test
                     {:source-paths ["src/cljc" "src/cljs" "test/cljs"]
                      :compiler
                      {:output-to "target/test.js"
                       :main "centipair.doo-runner"
                       :optimizations :whitespace
                       :pretty-print true}}}} 
                  
                  :figwheel
                  {:http-server-root "public"
                   :server-port 3449
                   :nrepl-port 7002
                   :nrepl-middleware ["cemerick.piggieback/wrap-cljs-repl"]
                   :css-dirs ["resources/public/css"]
                   :ring-handler centipair.handler/app}
                  :doo {:build "test"}
                  :source-paths ["env/dev/clj" "test/clj"]
                  :resource-paths ["env/dev/resources"]
                  :repl-options {:init-ns user}
                  :injections [(require 'pjstadig.humane-test-output)
                               (pjstadig.humane-test-output/activate!)]}
   :project/test {:resource-paths ["env/dev/resources" "env/test/resources"]}
   :profiles/dev {}
   :profiles/test {}})
