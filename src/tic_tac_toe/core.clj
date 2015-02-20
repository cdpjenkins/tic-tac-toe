(ns tic-tac-toe.core
  (:gen-class)
  (:use  [ring.adapter.jetty :only [run-jetty]])
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [hiccup.core :as hiccup]
            [hiccup.form :as form]
            [hiccup.element :as element]
            [ring.util.response :as resp]
            [ring.middleware.session :as session ]
            [ring.util.response :as response]
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
  [:o :e :e
   :e :o :o
   :o :e :e])

(def poses (range 9))

(defn place-piece [board piece pos]
  (assoc board pos piece))

(defn available-squares [board]
  (filter #(= :e (board %)) poses))

(defn print-board [board]
  (doseq [row (partition 3 board)]
    (println (apply str row))))




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

(defn get-game-state
  "Return one of :x, :o, :ongoing, :draw"
  ([board]
     (let [partitions (winning-partitions board)]
       (cond
        (player-has-won :x partitions) :x
        (player-has-won :o partitions) :o
        (empty? (available-squares board)) :draw
        :else :ongoing))))

;(winner blank-board)


;;0 1 2
;;3 4 5
;;6 7 8



(def toggle
  (let [piece (atom :o)
        toggle-fn (fn [piece]
                    (if (= piece :o)
                      :x
                      :o))]
    (fn [] (swap! piece toggle-fn))))


(defn get-next-move [board]
  (filter #(= :o (get-game-state % ))
    (for [pos-move (available-squares board)]
      (place-piece board :o pos-move))))



(get-next-move board-draw)


;;
;; Web stuff below this point!
;;

(defn get-board [board]
  (let [rows (partition 3 board)
        ;;piece (toggle)
        piece :x
        game-state (get-game-state board)]
    (hiccup/html [:center
                  [:table {:border "1px solid black"}
                   (for [[row-number row] (map-indexed vector rows)]
                     [:tr
                      (for [[column-number cell] (map-indexed vector row)]
                       [:td
                        (if (and (= :e cell) (= :ongoing game-state) )
                           [:a {:href (str "/place-piece/" (+  column-number (* row-number 3)) "/" (name piece) )}
                             (element/image (cell picture-map))]
                          (element/image (cell picture-map))
                          )

                        ])])]
                 (form/form-to [:post "/post"] (form/submit-button "new game" ))
                  [:h1 "game state: " (name game-state)]])))

(defn get-board-atom []
  (get-board @board))


(defroutes app-routes
  (GET "/" {session :session} ;(get-board-atom)
       (let [board (if (contains? session :board)
                      (:board session)
                      blank-board)]
         (response/content-type
          (assoc (response/response (get-board board))
            :session (assoc session :board board)) "text/html" )))
  (GET "/dump-session" {session :session} {:body (str "waaaaaa " session)
                                           :session (assoc session :1 1)})
  (GET "/place-piece/:pos/:piece" [pos piece]
       (do
         (swap! board place-piece (keyword piece) (Integer/parseInt pos))
         (when (= :ongoing (get-game-state @board))
           (swap! board place-piece :o (rand-nth (available-squares @board ))))

         (let [players-turn  (place-piece (keyword piece) (Integer/parseInt pos))
               computer-turn (place-piece :o (rand-nth (available-squares players-turn)))]

         (resp/redirect "/")))
  (POST "/post" [] (do
                     (reset! board blank-board)
                     (resp/redirect "/")))
  (route/resources "/")
  (route/not-found "<h1>Page not found</h1>"))

(def app
  (-> app-routes
      (handler/site)
      (session/wrap-session)))

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

(comment
  (def server (make-server))
  (.stop server)
  (swap! board place-piece :x 0)
  (swap! board place-piece :o 4)
)
