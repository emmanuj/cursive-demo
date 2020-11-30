(ns app.client.route-handler
  (:require [clj-http.client :as client]
            [clojure.string :as string]
            [shadow.http.push-state :as ps])
  (:import [org.apache.http NoHttpResponseException]))

(defn handler
  "Only proxy requests that starts with /api. All others should return index.html"
  [{:keys [uri http-config body headers request-method] :as request}]
  (if-not (string/starts-with? uri "/api")
    (ps/handle request)
    (try (client/request
           {:method request-method
            :url (str (:dev-server/proxy http-config) uri)
            :body body
            :headers (dissoc headers "content-length")
            :throw-exceptions false})
         (catch NoHttpResponseException e
           {:status 504
            :body (.getMessage e)}))))