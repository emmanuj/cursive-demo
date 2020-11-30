(ns app.server.server
  (:require
   [ring.middleware.not-modified :refer [wrap-not-modified]]
   [ring.middleware.resource :refer [wrap-resource]]
   [ring.middleware.json :refer [wrap-json-body]]
   [ring.middleware.keyword-params :refer [wrap-keyword-params]]
   [reitit.ring.middleware.muuntaja :as muuntaja]
   [reitit.ring.middleware.parameters :as parameters]
   [reitit.coercion.spec :as rcs]
   [muuntaja.core :as m]
   [immutant.web :as web]
   [taoensso.sente.server-adapters.immutant :refer [get-sch-adapter]]
   [taoensso.sente :as sente]
   [ring.middleware.anti-forgery :refer [wrap-anti-forgery]]
   [clojure.string :as str]
   [reitit.ring :as r]
   [reitit.ring.coercion :as rrc]
   [clojure.core.async :as async]
   [clj-uuid :as uuid]
   [com.stuartsierra.component :as component]))

(defn auth-middleware
  "Right now this is here so I can easily identify my testing clients"
  [profile handler]
  (fn [req]
    (when-not (= :prod profile)
      (handler (assoc req :user {:id (or (get-in req [:params :user-id]) (uuid/v1))})))))

(defn receive-push-loop!
  [{:keys [ch-recv] :as server} database]
  (async/go-loop []
    (when-let [push (async/<! ch-recv)]
      ;; (client/broadcast-diff! server (push/handle-push server database push))
      (recur))))

(defn app
  [profile {:keys [database]}]
  (let [{:keys [ch-recv send-fn connected-uids
                ajax-post-fn ajax-get-or-ws-handshake-fn]}
        (sente/make-channel-socket!
         (get-sch-adapter)
         (merge {:user-id-fn (fn [{:keys [uri user]}]
                               (let [[game-id _] (take-last 2 (str/split uri #"/"))]
                                 {:game-id (uuid/as-uuid game-id) :client-id (uuid/v1) :user-id (uuid/as-uuid (:id user))}))}
                (when-not (= :prod profile)
                  {:csrf-token-fn nil})))
        handler (r/ring-handler
                 (r/router
                  ["/api"
                   ["/status" {:get (fn [_]
                                      {:status 200
                                       :body   {:project-name "Cursive Demo"
                                                :version      "0.0.1"}})}]]
                  {:data {:middleware [parameters/parameters-middleware
                                       muuntaja/format-middleware
                                       wrap-not-modified
                                       #(wrap-resource % "public")
                                       ;;wrap-anti-forgery
                                       wrap-keyword-params
                                       (partial auth-middleware profile)
                                       rrc/coerce-exceptions-middleware
                                       rrc/coerce-request-middleware
                                       rrc/coerce-response-middleware]
                          :muuntaja   m/instance
                          :coercion   rcs/coercion}})

                 (r/create-default-handler))
        server {:server         (web/run handler {:host "0.0.0.0"
                                                  :port 3000})
                :handler        handler
                :ch-recv        ch-recv
                :send-fn        send-fn
                :connected-uids connected-uids}]
    (receive-push-loop! server database)
    server))

(defrecord Server [profile database]
  component/Lifecycle
  (start [this]
    (if (:server this)
      this
      (let [server (app profile database)]
        (merge this server))))

  (stop [this]
    (if-let [server (:server this)]
      (do
        (web/stop server)
        (async/close! (:ch-recv this))
        (assoc this :server nil
               :handler nil
               :ch-recv nil
               :send-fn nil
               :connected-uids nil))
      this)))

(defn new-server [profile]
  (component/using
   (map->Server {:profile profile})
   [:database]))
