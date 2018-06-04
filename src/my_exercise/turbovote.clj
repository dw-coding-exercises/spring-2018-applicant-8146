(ns my-exercise.turbovote
    (:require [hiccup.page :refer [html5]]
              [clojure.string :as string]
              [org.httpkit.client :as http]))

  (defn sanitize [data-string]
    "Sanatize a given string for submission to Turbovote's API."
    (assert (instance? String data-string) "Data is not java.lang.String.")
    (let [ds (-> data-string
                 (string/lower-case)
                 (string/trim))]
     (string/replace ds #" " "_")))

  (defn generate-ocd-query-string [params]
    "Takes location params and generates the query string for Turbovote."
    (let [state (sanitize (params :state))
          place (sanitize (params :city))
          ocd-string "ocd-division/country:us"
          ocd-string (if (not-empty state)
                         (string/join "" [ocd-string "," ocd-string "/state:" state])
                         ocd-string)
          ocd-string (if (and (not-empty place) (not-empty state))
                         (string/join "" [ocd-string "," ocd-string "/place:" place])
                         ocd-string)]
          ocd-string))

  (defn fetch-elections [params]
    "Make the request for the elections data from the Turbovote API. Returns EDN."
    "TODO: Gracefully handle an API failure, including alert for user."
    "TODO: Handle SSL here, we shouldn't be using the http-kit :insecure flag."
    "TODO: Slurp is hacky, we should have a custom reader / parser."
    (let [turbovote-url (string/join "" ["https://api.turbovote.org/elections/upcoming?district-divisions="
                                         (generate-ocd-query-string params)])
          {:keys [status headers body error] :as resp} @(http/get turbovote-url 
                                                                  {:insecure? true})]
         (if error
             (println error))
         (slurp body)))