(ns app.shared.game)

(defn apply-game-patch-values
  [game diff-as-patch-values]
  (reduce
    (fn [acc facet]
      (assoc-in acc (butlast facet) (last facet)))
    game
    diff-as-patch-values))
