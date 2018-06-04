(ns my-exercise.search
    (:require [hiccup.page :refer [html5]]
              [my-exercise.home :as home]
              [my-exercise.turbovote :as turbovote]
              [clojure.edn :as edn]))
  
  (defn header [_]
    [:head
     [:meta {:charset "UTF-8"}]
     [:meta {:name "viewport"
             :content "width=device-width, initial-scale=1.0, maximum-scale=1.0"}]
     [:title "Search results"]
     [:link {:rel "stylesheet" :href "default.css"}]])

  (defn validate [params]
    "Validates form data using primitive regex."
    "TODO: Update regex to be more sophisticated, make sure ZIP is 5 digits."
    "TODO: Extract this into the form file."
    (let [no-antiforgery-params (dissoc params :__anti-forgery-token)]
      (if (every? some? (map (fn [x] (re-find #"^[\w ]*$" x)) 
                             (vals no-antiforgery-params)))
          true
          false)))

  (defn elections-list [elections-edn]
    "Accepts an elections EDN and returns an unordered list of the results."
    "TODO: This is a dump; we should be pretty-printing this for user consumption."
    [:div (edn/read-string elections-edn)]
    )

  (defn valid-params-body [request]
    "Page content when form parameters are valid. "
    [:div {:class "valid-parameters"}
      [:h1 "Here's your election data. Good luck reading it!"]
      (-> (get request :params) (turbovote/fetch-elections) (elections-list))
      [:p "Want to look elections for another location?"]
      (home/address-form request)])
  
  (defn invalid-params-body [request]
    "Page content when invalid parameters are submitted to the form."
    [:div {:class "invalid-parameters"}
      [:h1 "Hm, that doesn't seem quite right..."]
      [:p "Some of the information you entered doesn't look quite right. Want to try again?"]
      (home/address-form request)])

  (defn body [request]
    "Returns the HTML for the body of the page."
    (if (validate (get request :params))
        (valid-params-body request)
        (invalid-params-body request)))
  
  (defn page [request]
    (html5
     (header request)
     (body request)))
  