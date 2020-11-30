(ns app.client.core
  (:require
   [helix.core :as hx :refer [$ <>]]
   [helix.dom :as d]
   [app.client.lib :as lib :refer [defnc]]
   [app.client.components :as c]
   [app.client.views.home :refer [Home]]
   [app.client.views.game :refer [Game]]
   [app.client.views.signup :refer [Signup]]
   [app.client.views.login :refer [Login]]
   [app.client.routes :refer [routes]]
   [app.client.theme :refer [theme]]
   ["react-dom" :as rdom]
   ["react-router-dom" :as rr]
   ["react-query-devtools" :as rqdt]
   ["@material-ui/core/CssBaseline" :default mui-CssBaseline]
   ["@material-ui/core/Container" :default mui-Container]
   ["@material-ui/core/styles" :refer (ThemeProvider)]
   ["@material-ui/core/Typography" :default Typography]
   ["@reduxjs/toolkit" :as rdt]
   ["@auth0/auth0-react" :as auth0]
   [react-redux :as redux]))

(def root-reducer (rdt/combineReducers #js {}))
(def store (rdt/configureStore #js {:reducer root-reducer}))

(defnc NotFound []
  ($ Typography {:variant "h3"} "Page Not Found"))

(defnc App
  []
  ($ auth0/Auth0Provider {:domain "emmanuj.us.auth0.com"
                          :clientId "w72X64qgQiWQ3aCuF34xd7LSbdqoORfK"
                          :redirectUri (.. js/window -location -origin)}
     ($ redux/Provider {:store store}
        ($ rr/BrowserRouter
           (c/react-query-cache-provider
            {:queryCache lib/query-cache}
            ($ rqdt/ReactQueryDevtools {:initialIsOpen false})
            ($ ThemeProvider {:theme theme}
               ($ mui-CssBaseline)
               (<>
                (c/app-header)
                ($ mui-Container
                   (d/div {:class "main"}
                          ($ rr/Switch
                             ($ rr/Route {:path (:home routes) :exact true :component Home})
                             ($ c/AuthenticatedRoute {:path (:game routes) :component ($ Game)})
                             ($ rr/Route {:path (:login routes) :exact true :component Login})
                             ($ rr/Route {:path (:signup routes) :exact true :component Signup})
                             ($ rr/Route {:path "*" :component NotFound})))))))))))

(defn ^:dev/after-load start
  []
  (rdom/render ($ App) (js/document.getElementById "app")))

(defn ^:export main
  []
  (start))

(defn ^:dev/before-load stop []
  (js/console.log "stop"))
