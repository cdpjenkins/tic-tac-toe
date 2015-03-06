(ns tic-tac-toe.core-test
  (:require [clojure.test :refer :all]
            [tic-tac-toe.core :refer :all]))


(def winning-board
  [:x :x :e
   :e :e :e
   :o :o :e])

(deftest test-score
  (testing ""
    (is (=  [2 100] (score-move winning-board :x 2)))
    (is (=  [3 1] (score-move winning-board :x 3)))))


(deftest test-choose-move
  (testing "test choose move.."
    (is (=  [2 100] (choose-move winning-board :x)))))

