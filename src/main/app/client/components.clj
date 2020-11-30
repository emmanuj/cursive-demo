(ns app.client.components
  (:require [helix.core :as helix]))

(defmacro react-query-cache-provider [& args]
  `(helix/$ ReactQueryCacheProvider ~@args))

(defmacro app-header [& args]
  `(helix/$ AppHeader ~@args))

(defmacro chat-input [& args]
  `(helix/$ ChatInput ~@args))

(defmacro chat-message [& args]
  `(helix/$ ChatMessage ~@args))

(defmacro chat-feed [& args]
  `(helix/$ ChatFeed ~@args))

(defmacro label [& args]
  `(helix/$ Label ~@args))
