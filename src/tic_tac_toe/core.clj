(ns tic-tac-toe.core
  (:gen-class)
  (:use  [ring.adapter.jetty :only [run-jetty]])
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [hiccup.core :as hiccup]
            [hiccup.form :as form]
            [hiccup.element :as element]
))

(def picture-map {:e "empty.png" :x "cross.png" :o "nought.png"})



(def blank-board
  [:e :e :e
   :e :e :e
   :e :e :e])

(def board (atom blank-board))

(def test-board1
  [:e :e :e
   :e :o :e
   :e :e :e])

(def board-x-wins
  [:x :e :o
   :e :x :o
   :e :e :x])

(def board-o-wins
  [:x :e :o
   :e :x :o
   :e :e :o])

(def board-draw
  [:x :o :o
   :o :x :x
   :x :o :o])

(def poses (range 9))

(defn place-piece [board piece pos]
  (assoc board pos piece))

(defn available-squares [board]
  (filter #(= \- (board %)) poses))

(defn print-board [board]
  (doseq [row (partition 3 board)]
    (println (apply str row))))


(defn get-board [board]
  (let [rows (partition 3 board)]
    (hiccup/html [:center
                  [:table {:border "1px solid black"}
                  (for [row rows]
                    [:tr (for [cell row]
                       [:td (element/image (cell picture-map))])])]
                 (form/form-to [:post "/post"] (form/submit-button "asdf" ))])))


(defn zip [rest]
  (apply map vector rest))

(defn winning-partitions [board]
  (let [rows (partition 3 board)
        cols (zip rows)
        diagonals [(apply concat (partition 1 4 board))
                   (->> board
                        (drop 2)
                        (partition 1 2)
                        (take 3)
                        (apply concat))]]
    (concat rows cols diagonals)))

;;
;; (apply concat (partition 1 4 (apply concat (map reverse (partition 3 (range 9))))))

(defn possible-moves [board player]
  (doseq [pos (available-squares board)]
    (println)
    (print-board (place-piece board player pos))))

(defn player-has-won [player partitions]
  (some (fn [partition] (every? #(= player %) partition)) partitions))

(defn winner
  "Return one of :x, :o, nil, :draw"
  ([board]
     (let [partitions (winning-partitions board)]
       (cond
        (player-has-won :x partitions) :x
        (player-has-won :o partitions) :o
        (empty? (available-squares board)) :draw
        :else nil))))

;(winner blank-board)


;;0 1 2
;;3 4 5
;;6 7 8




(defroutes app-routes
  (GET "/" [] (get-board board-o-wins))
  (POST "/post" [] (get-board board-draw ))
  (route/resources "/")
  (route/not-found "<h1>Page not found</h1>"))

(def app
  (-> app-routes
      (handler/site)))

(defn make-server
  ([]
     (make-server 8000))
  ([port]
     (let [port port]
       (run-jetty (var app) {:port port :join? false}))))

(defn -main
  ([]
     (make-server 8000))
  ([port]
     (make-server (Integer/parseInt port))))

