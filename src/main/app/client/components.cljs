(ns app.client.components
  (:require [helix.core :as hx :refer [$ <>]]
            [helix.dom :as d]
            [helix.hooks :as hh]
            [app.client.lib :refer [defnc] :as lib]
            [app.client.theme :refer [colors]]
            [clojure.string :as str]
            ["moment" :as moment]
            ["react-router-dom" :as rr]
            ["react-query" :as rq]
            ["@material-ui/core/styles" :refer [makeStyles]]
            ["@material-ui/core/Button" :default Button]
            ["@material-ui/core/Typography" :default Typography]
            ["@material-ui/core/Container" :default Container]
            ["@material-ui/core/Toolbar" :default Toolbar]
            ["@material-ui/core/Card" :default Card]
            ["@material-ui/core/CardContent" :default CardContent]
            ["@material-ui/core/Paper" :default Paper]
            ["@material-ui/core/Tooltip" :default Tooltip]
            ["@material-ui/core/Divider" :default Divider]
            ["@material-ui/core/IconButton" :default IconButton]
            ["@material-ui/core/Input" :default Input]
            ["@material-ui/core/Avatar" :default Avatar]
            ["@material-ui/icons/Send" :default SendIcon]
            ["@material-ui/icons/Assistant" :default QuickActionIcon]
            ["@auth0/auth0-react" :refer [useAuth0 withAuthenticationRequired]])
  (:require-macros [app.client.components]))

(def ReactQueryCacheProvider rq/ReactQueryCacheProvider)

(def header-styles
  (makeStyles
   (fn [theme]
     (clj->js
      {:grow    {:flexGrow 1}
       :toolbar {:padding    0
                 :minHeight  128}
       :buttonDiv {:display "flex"
                   "& > *" {:margin ((.-spacing theme) 1)}}}))))

(def chat-input-styles
  (makeStyles
   (fn [theme]
     #js
      {:content #js {:display "flex" :alignItems "center" :backgroundColor "#f3f6fb"}
       :paper
       #js
        {:flexGrow 1,
         :padding (.spacing theme 0.5 2)}
       :input #js {:width "100%"},
       :divider #js {:width 1, :height 24},
       :fileInput #js {:display "none"}})))

(def chat-feed-styles
  (makeStyles
   (fn [theme]
     (clj->js {:root {:flexGrow 1 :overflow "hidden" :maxHeight "100%"},
               :inner {:padding (.spacing theme 2)}}))))

(def chat-message-styles
  (makeStyles
   (fn [theme]
     (clj->js {:root {:marginBottom (.spacing theme 2)},
               :authUser
               {:display "flex",
                :justifyContent "flex-end",
                "& $body"
                {:backgroundColor (.. theme -palette -primary -main),
                 :color (.. theme -palette -primary -contrastText)}},
               :inner {:display "flex", :maxWidth 500},
               :avatar {:marginRight (.spacing theme 2)},
               :no-avatar {:marginLeft (.spacing theme 2)}
               :body
               {:backgroundColor (get-in colors ["grey" "100"]),
                :color (.. theme -palette -text -primary),
                :borderRadius (.. theme -shape -borderRadius),
                :padding (.spacing theme 1 2)},
               :content {:marginTop (.spacing theme 1)},
               :image
               {:marginTop (.spacing theme 2),
                :height "auto",
                :width 380,
                :maxWidth "100%"},
               :footer
               {:marginTop (.spacing theme 1),
                :display "flex",
                :justifyContent "flex-end"}}))))

(def label-styles
  (makeStyles
   (fn [theme]
     #js
      {:root
       #js
        {:display "inline-flex",
         :alignItems "center",
         :justifyContent "center",
         :flexGrow 0,
         :flexShrink 0,
         :borderRadius 2,
         :lineHeight "10px",
         :fontSize "10px",
         :height 20,
         :minWidth 20,
         :whiteSpace "nowrap",
         :padding (.spacing theme 0.5 1)},
       :rounded #js {:borderRadius 10, :padding (.spacing theme 0.5)}})))

(defnc AppHeader []
  (let [classes (lib/build-styles header-styles)
        authService (useAuth0)
        logoutWithRedirect (fn [] (.logout authService #js {:returnTo (.. js/window -location -origin)}))]
    ($ Container
       ($ Toolbar {:className (:toolbar classes)}
          (d/div {:className (:grow classes)}
                 ($ Typography {:variant "h2"} "Brisket Mafia"))
          (d/div {:className (:buttonDiv classes)}
                 (if (.-isAuthenticated authService)
                   (<>
                    (d/div "Welcome, " (.. authService -user -name))
                    ($ Button {:onClick #(logoutWithRedirect) :variant "outlined"} "Logout"))
                   (<> ($ Button {:onClick #(.loginWithRedirect authService) :variant "outlined"} "Login")
                       ($ Button {:onClick #(.loginWithRedirect authService #js {:screen_hint "signup"})
                                  :variant "outlined"} "Sign up"))))))))

(defnc ChatInput []
  (let [classes (chat-input-styles)
        [input-value set-input-value] (hh/use-state "")
        handle-change (hh/use-callback :auto-deps
                                       (fn [event]
                                         (.persist event)
                                         (set-input-value (.. event -target -value))))]
    ($ Card
       ($ CardContent {:className (.-content classes)}
          ($ Paper {:className (.-paper classes), :elevation 1}
             ($ Input {:className (.-input classes)
                       :onChange handle-change
                       :disableUnderline true
                       :placeholder "Type the message ..."}))
          ($ Tooltip {:title "Send"}
             ($ IconButton {:color (if (str/blank? input-value) "default" "primary")
                            :variant "contained"}
                ($ SendIcon)))
          ($ Divider {:className (.-divider classes)})
          ($ Tooltip {:title "Quick Action"}
             ($ IconButton {:edge "end", :onClick #(constantly {})}
                ($ QuickActionIcon)))))))

(defnc MessageContent [{:keys [content classes]}]
  (d/div {:className (:content classes)}
         (if (string? content)
           ($ Typography {:color "inherit" :variant "body1"} content)
           content)))

(defnc MessageFooter [{:keys [classes message]}]
  (d/div {:classzzName (:footer classes)}
         ($ Typography {:className (:footer classes) :variant "body2"}
            (str) (.fromNow (moment (:created-at message))))))
(defnc MessageAuthorName [{:keys [sender]}]
  (d/div (if (:auth-user sender) "Me" (:name sender))))

(declare ChatMessageGroup)

(defnc ChatMessage [{:keys [message className hide-avatar? hide-footer? hide-name?] :as _props}]
  (let [classes (lib/build-styles chat-message-styles)
        {:keys [sender content message-group?]} message]
    (if message-group?
      ($ ChatMessageGroup {:messages content})
      (d/div {:className (lib/class-names className
                                          (:root classes)
                                          {(:authUser classes) (get-in message [:sender :auth-user])})}
             (d/div {:className (:inner classes)}
                    (when-not hide-avatar? ($ Avatar {:className (:avatar classes)
                                                      :src (get-in message [:sender :avatar])}))
                    (d/div
                     (d/div {:className (lib/class-names {(:no-avatar classes) hide-avatar?} (:body classes))}
                            (when-not hide-name? ($ MessageAuthorName {:sender sender}))
                            ($ MessageContent {:content content :classes classes}))
                     (when-not hide-footer? ($ MessageFooter {:message message :classes classes}))))))))

(defnc ChatMessageGroup [{:keys [messages]}]
  (let [first-message-id (:id (first messages))
        last-message-id (:id (last messages))]
    (for [message messages]
      ($ ChatMessage {:key (:id message)
                      :message message
                      :hide-avatar? (not= (:id message) first-message-id)
                      :hide-footer? (not= (:id message) last-message-id)
                      :hide-name? (not= (:id message) first-message-id)}))))

(defnc ChatFeed [{:keys [messages className]}]
  (let [classes (lib/build-styles chat-feed-styles)]
    (d/div {:className (lib/class-names className (:root classes))}
           (d/div {:className (:inner classes)}
                  (for [message messages]
                    ($ ChatMessage {:message message :key (:id message)}))))))

(defnc Label [{:keys [className
                      variant
                      color
                      shape
                      children
                      style]}]
  (let [color (or color (get-in colors ["grey" "600"]))
        variant (or variant "contained")
        shape (or shape "square")
        classes (label-styles)
        rootClassName (lib/class-names (.-root classes) {(.-rounded classes) (= shape "rounded")} className)
        finalStyle (clj->js (merge (js->clj style) (if (= variant "contained")
                                                     {:backgroundColor color :color "#FFF"}
                                                     {:border (str "1px solid " color)
                                                      :color color})))]
    ($ Typography {:className rootClassName
                   :style finalStyle
                   :variant "overline"} children)))

;; Returns a component that redirects to the home page if user not authenticated
(defnc AuthenticatedComponent [{:keys [component] :as props}]
  {:wrap [(withAuthenticationRequired #js {:onRedirecting (fn [] (d/div {:style #js {:fontSize "26px"}} "Loading..."))})]}
  component)

;; Return a protected route
(defnc AuthenticatedRoute [{:keys [component] :as props}]
  (let [rest (dissoc props :component)]
    ($ rr/Route {& rest :render (fn [renderProps]
                                  ($ AuthenticatedComponent {:component component & renderProps}))})))
