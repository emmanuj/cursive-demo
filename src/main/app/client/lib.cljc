(ns app.client.lib
  (:require [clojure.string :as str])
  #?(:clj (:require [helix.core :as helix])
     :cljs (:require ["react-query" :as rq]
                     [clojure.walk :as walk]))
  #?(:cljs (:require-macros [app.client.lib])))

#?(:clj
   (defmacro defnc [type params & body]
     (let [opts? (map? (first body)) ;; whether an opts map was passed in
           opts (if opts?
                  (first body)
                  {})
           body (if opts?
                  (rest body)
                  body)
           ;; feature flags to enable by default
           default-opts {:helix/features {:fast-refresh true
                                          :check-invalid-hooks-usage true}}]
       `(helix.core/defnc ~type ~params
                          ;; we use `merge` here to allow indidivual consumers to override feature
                          ;; flags in special cases
          ~(merge default-opts opts)
          ~@body))))

#?(:cljs
   (def query-cache (new rq/QueryCache)))

(defn class-names-map [map]
  (reduce (fn [map-arr [key value]]
            (if (true? value)
              (conj map-arr key)
              map-arr)) [] map))

(defn class-names [& args]
  (str/join " "
            (mapv name
                  (reduce (fn [arr arg]
                            (cond
                              (or (string? arg)
                                  (symbol? arg)
                                  (keyword? arg)) (conj arr arg)
                              (vector? arg) (vec (concat arr arg))
                              (map? arg) (vec (concat arr (class-names-map arg)))
                              :else arr)) [] args))))

#?(:cljs (defn build-styles [use-styles-fn]
           (walk/keywordize-keys (js->clj (use-styles-fn)))))
