(ns liberator-service.server
  (:use [liberator.representation :only [wrap-convert-suffix-to-accept-header]]
        [ring.middleware.multipart-params :only [wrap-multipart-params]]
        ring.middleware.session.memory
        sandbar.stateful-session
        compojure.core 
        [compojure.handler :only [api]]
        hiccup.page
        hiccup.element        
        liberator-service.resources
        liberator-service.static-resources)
  (:require
   [ring.adapter.jetty :as jetty]))

(defonce my-session (atom {}))

(defn assemble-routes []
  (routes   
    (GET "/" [] home)       
    (POST "/login" [] login)
    (POST "/logout" [] logout)
    (GET "/user-info" [] user-info)
    (GET "/resources/:resource" [resource] static)))

(defn create-handler []
  (fn [request]
    ((->
       (assemble-routes)
       api
       wrap-multipart-params
       (wrap-stateful-session {:store (memory-store my-session)})       
       (wrap-convert-suffix-to-accept-header
         {".html" "text/html"
          ".txt" "text/plain"
          ".xhtml" "application/xhtml+xml"
          ".xml" "application/xml"
          ".json" "application/json"})) 
      request)))

(defn start [options]
  (jetty/run-jetty
   (fn [request]
     ((create-handler) request))
   (assoc options :join? false)))

(defn -main
  ([port]
     (start {:port (Integer/parseInt port)}))
  ([]
     (-main "8000")))
