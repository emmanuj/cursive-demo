(ns app.client.game
  (:require [ajax.core :as ajax]
            [reagent.core :as r]
            [taoensso.timbre :as timbre]
            [app.client.websocket :as websocket]
            [cljs.core.async :as async]))

(defn handler [response]
      (.log js/console (str response)))

(defn game-details []
      (let [state (r/atom {})] (ajax/POST "/api/v1/game" {:body {:name "test" :setup "test"}
                                  :handler handler})
           (ajax/GET "/api/v1/game/"
              {:response-format :transit
               :handler         (fn [response]
                                  (let [game-id (-> response
                                                    :data
                                                    first
                                                    :id
                                                    str)
                                        {:keys [ch-recv]} (websocket/make-connection game-id)]
                                    (async/go-loop []
                                      (let [push (async/<! ch-recv)]
                                        (let [{id :id data :?data} push]
                                          (when (= :chsk/recv id)
                                            (swap! state update :data conj (second data)))
                                          (recur))))))})


    (fn []
        [:div
         [:div "Stuff we've received"]
         [:div (:data @state)]])))


