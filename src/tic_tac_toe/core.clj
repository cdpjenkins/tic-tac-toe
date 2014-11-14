(ns tic-tac-toe.core
  (:gen-class))


(def blank-board
  [\- \- \-
   \- \- \-
   \- \- \-])

(def board (atom blank-board))

(def test-board1
  [\- \- \-
   \- \o \-
   \- \- \-])

(def board-x-wins
  [\x \- \o
   \- \x \o
   \- \- \x])

(def board-o-wins
  [\x \- \o
   \- \x \o
   \- \- \o])

(def board-draw
  [\x \o \o
   \o \x \x
   \x \o \o])

(def poses (range 9))

(defn place-piece [board piece pos]
  (assoc board pos piece))

(defn available-squares [board]
  (filter #(= \- (board %)) poses))

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

(defn winner
  "Return one of x, o, nil, :draw"
  ([board]
     (let [partitions (winning-partitions board)]
       (cond
        (player-has-won \x partitions) \x
        (player-has-won \o partitions) \o
        (empty? (available-squares board)) :draw
        :else nil))))


;;0 1 2
;;3 4 5
;;6 7 8
