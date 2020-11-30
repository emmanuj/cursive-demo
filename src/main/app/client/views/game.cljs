(ns app.client.views.game
  (:require [helix.core :refer [$ <>]]
            [helix.dom :as d]
            [app.client.lib :as lib :refer [defnc]]
            [app.client.theme :refer [colors]]
            [app.client.components :as c]
            [react-router-dom :refer [useParams]]
            ["moment" :as moment]
            ["@material-ui/core/Avatar" :default Avatar]
            ["@material-ui/core/Badge" :default Badge]
            ["@material-ui/core/Card" :default Card]
            ["@material-ui/core/CardHeader" :default CardHeader]
            ["@material-ui/core/CardContent" :default CardContent]
            ["@material-ui/core/Divider" :default Divider]
            ["@material-ui/core/Grid" :default Grid]
            ["@material-ui/core/IconButton" :default IconButton]
            ["@material-ui/core/List" :default List]
            ["@material-ui/core/ListItem" :default ListItem]
            ["@material-ui/core/ListItemAvatar" :default ListItemAvatar]
            ["@material-ui/core/ListItemText" :default ListItemText]
            ["@material-ui/core/Typography" :default Typography]
            ["@material-ui/icons/MoreVert" :default MoreIcon]
            ["@material-ui/styles" :refer [makeStyles]]))

(def useStyles (makeStyles (fn [theme]
                             (clj->js {:root {}
                                       :content {:paddingTop 0}
                                       :avatar {:height 40 :width 40}
                                       :listItem {:flexWrap "wrap"}
                                       :listItemText {:marginLeft ((.-spacing theme) 2)}
                                       :badge {:boxShadow (str "0 0 0 2px " (.-paper (.. theme -palette -background)))}
                                       :dot {:height 9 :minWidth 9}
                                       :onlineBadge {:backgroundColor (get-in colors ["green" "600"])}
                                       :onflineBadge {:backgroundColor (get-in colors ["grey" "300"])}
                                       :conversationRoot {:display "flex"
                                                          :flexDirection "column"
                                                          :backgroundColor (.-white (.. theme -palette -common))}
                                       :details {:marginLeft (.spacing theme 2)
                                                 :display "flex"
                                                 :flexDirection "column"
                                                 :alignItems "flex-end"}
                                       :unread {:marginTop 2 :padding 2 :height 18 :minWidth 18}}))))

(def players [{:id 1 :name "Ekaterina Tankova" :avatar "/images/avatars/avatar_2.png" :status :online}
              {:id 2 :name "Cao Yu" :avatar "/images/avatars/avatar_3.png" :status :online}
              {:id 3 :name "Enola Holmes" :avatar "/images/avatars/avatar_4.png" :status :online}
              {:id 4 :name "Mycroft Holmes" :avatar "/images/avatars/avatar_5.png" :status :online}])

(def channels [{:id 1 :name "Mafia"} {:id 2 :name "General" :unread 2}])

(defnc PlayersLists []
  (let [classes (lib/build-styles useStyles)]
    (<> ($ Card ($ CardHeader {:action ($ IconButton {:size "small"} ($ MoreIcon))
                               :title "Players"})
           ($ Divider)
           ($ CardContent {:className (:content classes)}
              ($ List {:disablePadding true}
                 (map-indexed (fn [idx {:keys [name id avatar status]}]
                                ($ ListItem {:disableGutters true
                                             :key id
                                             :divider (< idx (dec (count players)))
                                             :className (:listItem classes)}
                                   ($ ListItemAvatar
                                      ($ Badge {:classes #js {:dot (:dot classes)
                                                              :badge (lib/class-names (:badge classes)
                                                                                      {(:onlineBadge classes) (= status :online)
                                                                                       (:offlineBadge classes) (= status :offline)})}
                                                :anchorOrigin #js {:vertical "bottom"
                                                                   :horizontal "right"}
                                                :overlap "circle"
                                                :variant "dot"}
                                         ($ Avatar {:alt "player avatar" :className (:avatar classes) :src avatar})))
                                   ($ ListItemText {:className (:listItemText classes) :primary name})))
                              players)))))))

(def message {:id "1"
              :sender
              {:auth-user false
               :name "Adam Denisov"
               :avatar "/images/avatars/avatar_7.png"}
              :content
              "Hey, nice projects! I really liked the one in react. What's your quote on kinda similar project?"
              :created-at (.subtract (moment) 11 "hours")})

(def message2 {:id "2"
               :sender
               {:auth-user true
                :name "Adam Denisov"
                :avatar "/images/avatars/avatar_7.png"}
               :content
               "Hey, nice projects! I really liked the one in react. What's your quote on kinda similar project?"
               :created-at (.subtract (moment) 10 "hours")})

(def message3 {:id "4"
               :sender
               {:auth-user false
                :name "Adam Denisov"
                :avatar "/images/avatars/avatar_7.png"}
               :content
               "Hi"
               :created-at (.subtract (moment) 2 "hours")})

(def message4 {:id "5"
               :sender
               {:auth-user false
                :name "Adam Denisov"
                :avatar "/images/avatars/avatar_7.png"}
               :content
               "Hey?"
               :created-at (.subtract (moment) 2 "hours")})

(defnc ChannelsList []
  (let [classes (lib/build-styles useStyles)]
    (<> ($ Card ($ CardHeader {:action ($ IconButton {:size "small"} ($ MoreIcon))
                               :title "Channels"})
           ($ Divider)
           ($ CardContent {:className (:content classes)}
              ($ List {:disablePadding true}
                 (map-indexed (fn [idx {:keys [name id unread]}]
                                ($ ListItem {:disableGutters true
                                             :key id
                                             :divider (< idx (dec (count channels)))
                                             :className (:listItem classes)
                                             :button true}
                                   ($ ListItemAvatar
                                      ($ Avatar {:alt "player avatar" :className (:avatar classes) :size "small"}
                                         (subs name 0 2)))
                                   ($ ListItemText {:className (:listItemText classes) :primary name})
                                   (d/div {:className (:details classes)}
                                          (when (> unread 0)
                                            (c/label {:className (:unread classes)
                                                      :color (get-in colors ["red" "500"])
                                                      :shape "rounded"} unread)))))
                              channels)))))))

(defnc GameChat []
  ($ Card
     ($ CardContent
        (c/chat-feed {:messages [message message2 message3 message4]})
        (c/chat-input))))

(defnc Game []
  ($ Grid {:container true :spacing 2}
     ($ Grid {:item true :xs 4}
        ($ Grid {:direction "column" :spacing 2 :container true}
           ($ Grid {:xs 12 :item true} ($ PlayersLists))
           ($ Grid {:xs 12 :item true} ($ ChannelsList))))
     ($ Grid {:item true :xs 8}
        ($ GameChat))))
