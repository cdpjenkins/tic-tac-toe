(ns tic-tac-toe.game)

(def blank-board
  [:e :e :e
   :e :e :e
   :e :e :e])

;(def winning-board
;  [:x :x :o
;   :e :x :e
;   :o :o :e])

(def poses (range 9))


(defn available-squares [board]
  (filter #(= :e (board %)) poses))

(defn zip [rest]
  (apply map vector rest))

(defn place-piece [board piece pos]
  (assoc board pos piece))

(defn winning-partitions [board]
 ;(println board)
  (let [rows (partition 3 board)
        cols (zip rows)
        diagonals [(apply concat (partition 1 4 board))
                   (->> board
                        (drop 2)
                        (partition 1 2)
                        (take 3)
                        (apply concat))]]
    (concat rows cols diagonals)))

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

;; who is the opposite player?
(defn other-player [who]
  (if (= who :x) :o :x))

(declare choose-move)

(defn score-board [board who]
  (condp = (get-game-state board)
    who 100
    (other-player who) -100
    :draw 0
    :ongoing (let [other-players-move (choose-move board (other-player who))
                   score (second other-players-move)]
                  (* -1 score))))

(defn score-move [board who place]
  (let [new-board (place-piece board who place)
        score (score-board new-board who)]
  [place score]))


(defn choose-move [board who]
  (let [open-spaces (available-squares board)
        weighted-moves          (map #(score-move board who %)  open-spaces)
        best-move (first (sort-by second > weighted-moves))  ]
  ; (println weighted-moves)
   best-move))

(def choose-move (memoize choose-move))


;(choose-move winning-board :x)

(defn choose-really-good-move [board who]
  (let [best-score (choose-move board who)]
         (if-let [score (= 100 (second best-score))]
            (first best-score)
            (first (choose-move board (other-player who))))))

(def upgraded-rgm (memoize choose-really-good-move))


(comment
  (clojure.core.typed/check-ns )

  )
