(ns liberator-service.ui
  (:use hiccup.page
        hiccup.element
        hiccup.form
        sandbar.stateful-session
        liberator-service.resources
        [liberator.core :only [defresource]]))

(defn get-user []
  (first (filter #(= (session-get :user) (get-in % [:user])) users)))

(def login-page 
  [:body
   [:div#message]
   [:div#login
    (text-field "user")
    (password-field "pass")
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