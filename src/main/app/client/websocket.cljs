(ns app.client.websocket
  (:require [taoensso.sente :as sente]))

(def ?csrf-token
  (when-let [el (.getElementById js/document "sente-csrf-token")]
    (.getAttribute el "data-csrf-token")))

(defn make-connection
  [game-id]
  (let [{:keys [chsk ch-recv send-fn state]}
        (sente/make-channel-socket-client!
            (str "/api/v1/game/" game-id "/chsk")
            ?csrf-token
            {:type :ws
             :port 3000})]
    {:chsk chsk
     :ch-recv ch-recv
     :send-fn send-fn
     :state state}))