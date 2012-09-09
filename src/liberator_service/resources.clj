(ns liberator-service.resources
  (:use clojure.data.json                        
        sandbar.stateful-session
        [liberator.core :only [defresource request-method-in]]))

(def users [{:user "foo" 
             :pass "bar"
             :firstname "John"
             :lastname "Doe"}])

(defn valid-user [user]
  (some #(= user (select-keys % [:user :pass])) users))

(defresource login
  :available-media-types ["application/json" "text/javascript"]
  :method-allowed? (request-method-in :post)  
  :authorized?     (fn [{{user :params} :request}]                 
                     (or (session-get :user) (valid-user user)))
  
  :post! (fn [{{{:keys [user]} :params} :request :as ctx}]
           (session-put! :user user))
  
  :handle-unauthorized (fn [ctx] (:message ctx))  
  :handle-created      (json-str {:message "login successful"}))

(defresource logout
  :available-media-types ["application/json" "text/javascript"]
  :method-allowed? (request-method-in :post)  
  :post!           (session-delete-key! :user)
  :handle-created  (json-str {:message "logout successful"}))
