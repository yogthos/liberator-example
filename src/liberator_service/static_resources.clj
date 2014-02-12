(ns liberator-service.static-resources  
  (:use [liberator.core :only [defresource]]
        [ring.util.mime-type :only [ext-mime-type]])
  (:require [clojure.java.io :as io]))

(let [static-dir  (io/file "resources/public/")]
  (defresource static
    :available-media-types
    #(let [file (get-in % [:request :route-params :resource])]       
       (if-let [mime-type (ext-mime-type file)]
         [mime-type]
         []))

    :exists?
    #(let [file (get-in % [:request :route-params :resource])]       
       (let [f (io/file static-dir file)]
         [(.exists f) {::file f}]))
    
    :handle-ok (fn [{{{file :resource} :route-params} :request}]                 
                 (io/file static-dir file))

    :last-modified (fn [{{{file :resource} :route-params} :request}]                                                               
                     (.lastModified (io/file static-dir file))))
