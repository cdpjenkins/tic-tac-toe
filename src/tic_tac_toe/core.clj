(ns tic-tac-toe.core
  (:gen-class)
  (:use  [ring.adapter.jetty :only [run-jetty]]
         [tic-tac-toe.game])
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [hiccup.core :as hiccup]
            [hiccup.form :as form]
            [hiccup.page :as page]
            [hiccup.element :as element]
            [ring.util.response :as resp]
            [ring.middleware.session :as session ]
            [ring.util.response :as response]))

(def picture-map {:e "/empty.png" :x "/cross.png" :o "/nought.png"})


;;
;; Web stuff below this point!
;;

(defn get-cljs-page []
   (hiccup/html [:head
                 [:title "tic-tac-toe"]
                 (page/include-js "/js/cljs.js")]
               [:body
                [:div {:id "board"}]]))

(defn get-board [board]
  (let [rows (partition 3 board)
        game-state (get-game-state board)]
    (hiccup/html [:center
                  [:table {:border "1px solid black"}
                   (for [[row-number row] (map-indexed vector rows)]
                     [:tr
                      (for [[column-number cell] (map-indexed vector row)]
                       [:td
                        (if (and (= :e cell) (= :ongoing game-state) )
                           [:a {:href (str "/place-piece/" (+  column-number (* row-number 3)) "/x")}
                             (element/image (cell picture-map))]
                          (element/image (cell picture-map))
                          )

                        ])])]
                 (form/form-to [:post "/post"] (form/submit-button "new game" ))
                  [:h1 "game state: " (name game-state)]])))

(defn get-response [board session]
  (response/content-type
            (assoc (response/response (get-board board))
                                      :session (assoc session :board board))
            "text/html"))


(defroutes app-routes
  (GET "/" {session :session} ;(get-board-atom)
       (let [board (if (contains? session :board)
                       (:board session)
                       blank-board)]
         (response/content-type
          (assoc (response/response (get-board board))
            :session (assoc session :board board))
          "text/html" )))
  (GET "/place-piece/:pos/:piece"  {{pos :pos piece :piece} :params
                                     session :session}

         (let [players-turn  (place-piece (:board session) (keyword piece) (Integer/parseInt pos))]
           (if (or (= :x (get-game-state players-turn)) (= :draw (get-game-state players-turn)))
             ( get-response players-turn session)
             (let [ computer-turn (place-piece players-turn :o (first (choose-move players-turn :o)))]
               (get-response computer-turn session)))))
  (GET "/cljs" [] (get-cljs-page))
  (POST "/post"  {session :session}
          (assoc (response/redirect-after-post "/")
            :session (assoc session :board blank-board)))
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
