(ns app.client.theme
  (:require ["@material-ui/core/colors" :as mui-colors]
            ["@material-ui/core/styles" :refer [createMuiTheme]]))

(def white "#FFFFFF")

(def colors (js->clj mui-colors))

;; Palette

(def palette
  (clj->js {:primary {:contrastText white
                      :dark (get-in colors ["indigo" "900"])
                      :main (get-in colors ["indigo" "500"])
                      :light (get-in colors ["indigo" "100"])}
            :secondary {:contrastText white
                        :dark (get-in colors ["blue" "900"])
                        :main (get-in colors ["blue" "A700"])
                        :light (get-in colors ["blue" "A400"])}
            :error {:contrastText white
                    :dark (get-in colors ["red" "900"])
                    :main (get-in colors ["red" "600"])
                    :light (get-in colors ["red" "400"])}
            :text {:primary (get-in colors ["blueGrey" "900"])
                   :secondary (get-in colors ["blueGrey" "600"])
                   :link (get-in colors ["blue" "600"])}
            :link (get-in colors ["blue" "800"])
            :icon (get-in colors ["blueGrey" "600"])
            :background {:default "#F4F6F8" :paper white}
            :divider (get-in colors ["grey" "200"])}))

;; Typography

(def typography (clj->js {:h1
                          {:color (.. palette -text -primary)
                           :fontWeight 500
                           :fontSize "35px"
                           :letterSpacing "-0.24px"
                           :lineHeight "40px"}
                          :h2
                          {:color (.. palette -text -primary)
                           :fontWeight 500
                           :fontSize "29px"
                           :letterSpacing "-0.24px"
                           :lineHeight "32px"}
                          :h3
                          {:color (.. palette -text -primary)
                           :fontWeight 500
                           :fontSize "24px"
                           :letterSpacing "-0.06px"
                           :lineHeight "28px"}
                          :h4
                          {:color (.. palette -text -primary)
                           :fontWeight 500
                           :fontSize "20px"
                           :letterSpacing "-0.06px"
                           :lineHeight "24px"}
                          :h5
                          {:color (.. palette -text -primary)
                           :fontWeight 500
                           :fontSize "16px"
                           :letterSpacing "-0.05px"
                           :lineHeight "20px"}
                          :h6
                          {:color (.. palette -text -primary)
                           :fontWeight 500
                           :fontSize "14px"
                           :letterSpacing "-0.05px"
                           :lineHeight "20px"}
                          :subtitle1
                          {:color (.. palette -text -primary)
                           :fontSize "16px"
                           :letterSpacing "-0.05px"
                           :lineHeight "25px"}
                          :subtitle2
                          {:color (.. palette -text -secondary)
                           :fontWeight 400
                           :fontSize "14px"
                           :letterSpacing "-0.05px"
                           :lineHeight "21px"}
                          :body1
                          {:color (.. palette -text -primary)
                           :fontSize "14px"
                           :letterSpacing "-0.05px"
                           :lineHeight "21px"}
                          :body2
                          {:color (.. palette -text -secondary)
                           :fontSize "12px"
                           :letterSpacing "-0.04px"
                           :lineHeight "18px"}
                          :button #js {:color (.. palette -text -primary) :fontSize "14px"}
                          :caption
                          {:color (.. palette -text -secondary)
                           :fontSize "11px"
                           :letterSpacing "0.33px"
                           :lineHeight "13px"}
                          :overline
                          {:color (.. palette -text -secondary)
                           :fontSize "11px"
                           :fontWeight 500
                           :letterSpacing "0.33px"
                           :lineHeight "13px"
                           :textTransform "uppercase"}}))

;; Overrides
(def overrides (clj->js {"MuiButton" {:contained
                                      {:boxShadow "0 1px 1px 0 rgba(0,0,0,0.14)"
                                       :backgroundColor (get-in colors ["grey" "100"])
                                       "&:hover" {:backgroundColor (get-in colors ["grey" "300"])}}}
                         "MuiCardActions" {:root {:padding "16px 24px"}}
                         "MuiCardContent" {:root {:padding 24}}
                         "MuiCardHeader" {:root {:padding "16px 24px"}}
                         "MuiChip" {:root {:backgroundColor (get-in colors ["blueGrey" "50"])
                                           :color (get-in colors ["blueGrey" "900"])}
                                    :deletable {"&:focus" {:backgroundColor (get-in colors ["blueGrey" "100"])}}}
                         "MuiIconButton" {:root
                                          {:color (.-icon palette)
                                           "&:hover" {:backgroundColor "rgba(0,0,0,0.03)"}}}
                         "MuiInputBase" {:root {}
                                         :input {"&::placeholder" {:opacity 1
                                                                   :color (.. palette -text -secondary)}}}
                         "MuiLinearProgress" {:root  {:borderRadius 3 :overflow "hidden"}
                                              :colorPrimary {:backgroundColor (get-in colors ["blueGrey" "50"])}}
                         "MuiListItem" {:button {"&:hover" {:backgroundColor "rgba(0, 0, 0, 0.04)"}}}
                         "MuiListItemIcon" {:root {:color (.-icon palette) :minWidth 32}}
                         "MuiOutlinedInput" {:root {} :notchedOutline {:borderColor "rgba(0,0,0,0.15)"}}
                         "MuiPaper" {:root {}
                                     :elevation1
                                     {:boxShadow
                                      "0 0 0 1px rgba(63,63,68,0.05), 0 1px 3px 0 rgba(63,63,68,0.15)"}}
                         "MuiTableCell" {:root (assoc (js->clj (.-body1 typography))
                                                      :borderBottom (str "1p solid " (.-divider palette)))}
                         "MuiTableHead" {:backgroundColor (get-in colors ["grey" "50"])}
                         "MuiTableRow" {:root
                                        {"&$selected" {:backgroundColor (.. palette -background -default)}
                                         "&$hover" {"&:hover" {:backgroundColor (.. palette -background -default)}}}}
                         "MuiToggleButton" {:root
                                            {:color (.-icon palette)
                                             "&:hover" {:backgroundColor "rgba(208, 208, 208, 0.20)"}
                                             "&$selected"
                                             {:backgroundColor "rgba(208, 208, 208, 0.20)"
                                              :color (.. palette -primary -main)
                                              "&:hover" {:backgroundColor "rgba(208, 208, 208, 0.30)"}}
                                             "&:first-child" {:borderTopLeftRadius 4 :borderBottomLeftRadius 4}
                                             "&:last-child" {:borderTopRightRadius 4 :borderBottomRightRadius 4}}}
                         "MuiTypography" {:gutterBottom {:marginBottom 8}}}))
(def baseTheme
  (clj->js #js {:palette palette :typography typography :overrides overrides}))

(def theme (createMuiTheme baseTheme))
