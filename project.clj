(defproject liberator-example "0.1.0-SNAPSHOT"
  :description "Example for the Liberator library"
  :url "https://github.com/yogthos/liberator-example"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [compojure "1.0.2"]
                 [liberator "0.5.0"]
                 [sandbar "0.4.0-SNAPSHOT"]
                 [org.clojure/data.json "0.1.2"]
                 [ring/ring-jetty-adapter "1.1.0"]]
  :dev-dependencies [[lein-ring "0.7.3"]]
  :ring {:handler liberator-service.server/handler
         :adapter {:port 8000}}
  :main liberator-service.server)
