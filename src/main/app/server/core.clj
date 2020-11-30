(ns app.server.core
  (:require [com.stuartsierra.component :as component]
            [app.server.server :as server]
            [taoensso.timbre :as timbre]))

(defonce system nil)

(defn new-system
  "Creates a new System and allows specifying specifically which components to mount and for which
  profile."
  ([profile]
   (new-system profile [:server :database]))
  ([profile components]
   (apply component/system-map
          (apply concat
                 (-> {:database nil
                      :server (server/new-server profile)}
                     (select-keys components))))))

(defn stop!
  "Stops the current System."
  []
  (alter-var-root #'system (fn [s] (when s (component/stop s) nil))))

(def shutdown-hook (Thread. ^Runnable (fn []
                                        (timbre/info "Received shutdown hook")
                                        (stop!))))
(defn start!
  "Starts the System up."
  []
  (.removeShutdownHook (Runtime/getRuntime) shutdown-hook)
  (.addShutdownHook (Runtime/getRuntime) shutdown-hook)
  (stop!)
  (alter-var-root #'system (constantly (new-system :dev)))
  (alter-var-root #'system component/start))
