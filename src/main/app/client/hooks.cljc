(ns app.client.hooks
  #?(:clj (:require [helix.core :as helix])
     :cljs (:require ["react-query" :as rq]
                     [camel-snake-kebab.core :as csk]
                     [camel-snake-kebab.extras :as cske]))
  #?(:cljs (:require-macros [app.client.hooks])))

#?(:cljs
   (def use-query-cache rq/useQueryCache))

#?(:cljs
   (defn use-query
     "Wrapper arround React Query's useQuery that allows passing clojure map with kebab-cased keywords"
     ([query-key query-fn]
      (use-query query-key query-fn {}))
     ([query-key query-fn config]
      (use-query {:query-key query-key :query-fn query-fn :config config}))
     ([{:keys [query-key query-fn config]}]
      (let [query-config (-> (cske/transform-keys csk/->camelCase config)
                             (clj->js))]
        (->> (rq/useQuery #js {:queryKey query-key :queryFn query-fn :config query-config})
             (js->clj)
             (cske/transform-keys csk/->kebab-case-keyword))))))
