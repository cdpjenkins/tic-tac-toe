(ns tic-tac-toe.core-test
  (:require [clojure.test :refer :all]
            [tic-tac-toe.core :refer :all]))


(def winning-board1
  [:x :x :e
   :e :e :e
   :o :o :e])

(def winning-board2
  [:x :x :o
   :e :x :e
   :o :o :e])

(def defending-board1
  [:x :e :o
   :e :x :e
   :o :e :e])

(def defending-board2
  [:x :e :o
   :x :e :o
   :e :e :e])

(deftest test-score
  (testing ""
    (is (=  [2 100] (score-move winning-board1 :x 2)))
    (is (=  [3 -100] (score-move winning-board1 :x 3)))))

(deftest test-choose-move
  (testing "test choose move.."
    (is (=  [2 100] (choose-move winning-board1 :x)))
    (is (=  [8 100] (choose-move winning-board2 :x)))
    (is (=  [8 100] (choose-move winning-board2 :o)))
    (is (=  [8 100] (choose-move winning-board1 :o)))

    ))


(deftest test-really-good-move
  (testing ""
    (is (= 8 (choose-really-good-move defending-board1 :o)))
    (is (= 8 (choose-really-good-move defending-board2 :o)))
    ))
