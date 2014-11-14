(ns tic-tac-toe.core
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

(def blank-board
  [\- \- \-
   \- \- \-
   \- \- \-])

(def board (atom blank-board))

(def test-board1
  [\- \- \-
   \- \o \-
   \- \- \-])


(def poses (range 9))

(defn place-piece [board piece pos]
  (assoc board pos piece))

(defn available-squares [board]
  (filter #(= \- (board %)) poses))

(defn print-board [board]
  (doseq [row (partition 3 board)]
    (println (apply str row))))

(defn possible-moves [board player]
  (doseq [pos (available-squares board)]
    (println)
    (print-board (place-piece board player pos))))
