(ns tic-tac-toe.core
  (:require [domina :as d]
            [domina.xpath :as x ]
            [domina.events :as ev]
            [hiccups.runtime :as hiccupsrt])
  (:require-macros [hiccups.core :as h]))

(enable-console-print!)

(def picture-map {:e "/empty.png" :x "/cross.png" :o "/nought.png"})

(def game-state (atom []))

(defn init []
  (println "hello from clojureScript!!")
  (d/append! (x/xpath "//body") "<div>Hello world!</div>")
  (d/append! (d/by-id "board") (h/html [:center
                  [:table {:border "1px solid black"}
                   (for [row-number (range 3)]
                     [:tr
                      (for [column-number (range 3)]
                       [:td
                          [:div {:id (str (+ (* 3 row-number) column-number)) } [:img {:src (:e picture-map)}]]

                        ])])]
                  [:h1 "game state: " ]]  ))

  (doseq [i (range 9)] (ev/listen! (d/by-id (str i )) :click (fn [evt] (println "button clicked!" i))))

  )

(set! (.-onload js/window) init)
