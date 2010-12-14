;   Copyright (c) Rich Hickey. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file epl-v10.html at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

; Author: Frantisek Sodomka, Stephen C. Gilardi


(ns clojure.test-clojure.vars
  (:use clojure.test))

; http://clojure.org/vars

; def
; defn defn- defonce

; declare intern binding find-var var

(def ^:dynamic a)
(deftest test-binding
  (are [x y] (= x y)
      (eval `(binding [a 4] a)) 4     ; regression in Clojure SVN r1370
  ))

; var-get var-set alter-var-root [var? (predicates.clj)]
; with-in-str with-out-str
; with-open

(deftest test-with-local-vars
  (let [factorial (fn [x]
                    (with-local-vars [acc 1, cnt x]
                      (while (> @cnt 0)
                        (var-set acc (* @acc @cnt))
                        (var-set cnt (dec @cnt)))
                      @acc))]
    (is (= (factorial 5) 120))))

;;;(deftest test-with-precision
;;;  (are [x y] (= x y)
;;;       (with-precision 4 (+ 3.5555555M 1)) 4.556M
;;;       (with-precision 6 (+ 3.5555555M 1)) 4.55556M
;;;       (with-precision 6 :rounding CEILING     (+ 3.5555555M 1)) 4.55556M
;;;       (with-precision 6 :rounding FLOOR       (+ 3.5555555M 1)) 4.55555M
;;;       (with-precision 6 :rounding HALF_UP     (+ 3.5555555M 1)) 4.55556M
;;;       (with-precision 6 :rounding HALF_DOWN   (+ 3.5555555M 1)) 4.55556M
;;;       (with-precision 6 :rounding HALF_EVEN   (+ 3.5555555M 1)) 4.55556M
;;;       (with-precision 6 :rounding UP          (+ 3.5555555M 1)) 4.55556M
;;;       (with-precision 6 :rounding DOWN        (+ 3.5555555M 1)) 4.55555M
;;;       (with-precision 6 :rounding UNNECESSARY (+ 3.5555M 1))    4.5555M))

;;;(deftest test-settable-math-context
;;;  (is (=
;;;       (clojure.main/with-bindings
;;;         (set! *math-context* (java.math.MathContext. 8))
;;;         (+ 3.55555555555555M 1))
;;;       4.5555556M)))

; set-validator get-validator

; doc find-doc test

(deftest test-with-redefs-fn
  (let [p (promise)]
    (with-redefs-fn {#'nil? :temp}
      (fn []
        (.Start (System.Threading.Thread. (gen-delegate System.Threading.ThreadStart [] (deliver p nil?))))  ;;; (.start (Thread. #(deliver p nil?)))
        @p))
    (is (= :temp @p))
    (is (not= @p nil?))))

(deftest test-with-redefs
  (let [p (promise)]
    (with-redefs [nil? :temp]
      (.Start (System.Threading.Thread. (gen-delegate System.Threading.ThreadStart [] (deliver p nil?))))  ;;; (.start (Thread. #(deliver p nil?)))
      @p)
    (is (= :temp @p))
    (is (not= @p nil?))))

(deftest test-with-redefs-throw
  (let [p (promise)]
    (is (thrown? Exception
      (with-redefs [nil? :temp]
        (deliver p nil?)
        (throw (Exception. "simulated failure in with-redefs")))))
    (is (= :temp @p))
    (is (not= @p nil?))))