(ns liberator-service.resources
  (:use clojure.data.json        
        hiccup.page
        hiccup.element
        hiccup.form
        ring.middleware.session
        ring.util.response
        sandbar.stateful-session
        [liberator.core :only [defresource request-method-in]]))

(def users [{:user "foo" 
             :pass "bar"
             :firstname "John"
             :lastname "Doe"}])

(defn valid-user [user]
  (some #(= user (select-keys % [:user :pass])) users))

(defn get-user []
  (first (filter #(= (session-get :user) (get-in % [:user])) users)))


(def login-page 
  [:body
   [:div#message]
   [:div#login
    (text-field "user")
    (text-field "pass")
    [:button {:type "button" :onclick "login()"} "login"]]])

(defn home-page [] 
  [:body
   (let [{firstname :firstname lastname :lastname} (get-user)] 
     [:div#message (str "Welcome " firstname " " lastname)])
   [:div#logout 
    [:button {:type "button" :onclick "logout()"} "logout"]]])

(defresource home
  :available-media-types ["text/html"]
  :available-charsets ["utf-8"]
  :handle-ok (html5 [:head (include-js
                             "http://ajax.googleapis.com/ajax/libs/jquery/1.8.0/jquery.min.js"
                             "/resources/site.js")]
                    (if (session-get :user) (home-page) login-page)))

(defresource user-info
  :available-media-types ["application/json" "text/javascript"]
  :method-allowed? (fn [ctx]
                     (and (session-get :user) (request-method-in :get)))
  :handle-ok (fn [ctx]
               (->> users
                 (filter #(= (session-get :user) (get-in % [:user])))
                 first
                 json-str)))

(defresource login
  :available-media-types ["application/json" "text/javascript"]
  :method-allowed? (request-method-in :post)  
  :authorized? (fn [{{user :params} :request}]                 
                 (or (session-get :user) (valid-user user)))
  
  :post! (fn [{{{:keys [user]} :params} :request :as ctx}]
           (session-put! :user user))
  
  :handle-unauthorized (fn [ctx] (:message ctx))  
  :handle-created (fn [ctx]                    
                    (response (json-str {:message "login successful"}))))

(defresource logout
  :available-media-types ["application/json" "text/javascript"]
  :method-allowed? (fn [{{{user :user} :session} :request}] 
                     (request-method-in :post))  
  :post!           (fn [ctx] (session-delete-key! :user))
  :handle-created  (json-str {:message "logout successful"}))
