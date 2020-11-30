(ns app.client.views.home
  (:require [helix.dom :as d]
            [helix.core :refer [$ <>]]
            [app.client.lib :refer [defnc]]
            [app.client.hooks :as hk]
            [app.client.components :as c]
            ["@material-ui/core/Typography" :default Typography]))

;; TODO:
;;   - On home page, be able to start a new game
;;   - Find games
;;   - Join games
;;
(declare Example)

(defnc Home []
  (<> ($ Typography {:variant "h3"} "Home Page 1")
      ($ Example)))

(defnc Example []
  (let [{:keys [is-loading is-error error data]}
        (hk/use-query "repoData"
                      (fn []
                        (-> (js/fetch "https://reqres.in/api/unknown")
                            (.then (fn [resp]
                                     (.json resp))))))]
    (cond

      is-loading
      (d/span "loading...")

      is-error
      (d/span "Error: " (:message error))

      :else
      (when (seq data)
        (d/ul
         (for [item (:data data)]
           (d/li {:key (:id item)} (:name item))))))))
