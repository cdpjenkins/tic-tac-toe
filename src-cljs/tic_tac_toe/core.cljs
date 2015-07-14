(ns tic-tac-toe.core
  (:require [domina :as d]
            [domina.xpath :as x ]
            [domina.events :as ev]
            [hiccups.runtime :as hiccupsrt]
            [tic-tac-toe.game :as game])
  (:require-macros [hiccups.core :as h]))

(enable-console-print!)

(def picture-map {:e "/empty.png" :x "/cross.png" :o "/nought.png"})

(def game-state (atom []))

(defn new-game! []
  (reset! game-state game/blank-board))

(defn update-dom [board]
  (doseq [i (range 9)]
    (let [value (board i)
          node (d/by-id (str i))]
      (d/set-attr! node :src (value picture-map))
      (d/set-text! (d/by-id "game-state") (str "Game state: " (game/get-game-state board))))))

(defn make-move [board pos]
  (let [piece :x
        players-turn  (game/place-piece board piece pos)
        state (game/get-game-state players-turn)]
    ;(println state)
           (if (or (= :x state)
                   (= :draw state))
             players-turn
             (let [computer-turn (game/place-piece players-turn :o
                                                   (first (game/choose-move players-turn :o)))]
               computer-turn))))

(defn init []
  (new-game!)
  (d/append! (d/by-id "board") (h/html [:center
                  [:table {:border "1px solid black"}
                   (for [row-number (range 3)]
                     [:tr
                      (for [column-number (range 3)]
                       [:td
                          [:div [:img {:id (str (+ (* 3 row-number) column-number))
                                       :src (:e picture-map)}]]
                        ])])]
                  ;(form/form-to [:post "/post"] (form/submit-button "new game" ))
                  [:button {:id "button" } "new game"]
                  [:h1 {:id "game-state"} "Game state: " (game/get-game-state @game-state)]]))

  (doseq [i (range 9)]
    (ev/listen!
     (d/by-id (str i ))
     :click
     (fn [evt] (println "square clicked!" i)
          (when (and (= :e (get @game-state i))
                    (= :ongoing (game/get-game-state @game-state)))
           (swap! game-state make-move i)
           (println @game-state (game/get-game-state @game-state))
           (update-dom @game-state)))))
  (ev/listen!
   (d/by-id "button")
   :click (fn [evt] (println "now the real button has been clicked")
            (new-game!)
            (update-dom @game-state)))
  )

(set! (.-onload js/window) init)
