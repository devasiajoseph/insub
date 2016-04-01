(ns libs.centipair.utilities.clean
  (:require [libs.centipair.utilities.validators :as v]))


(defn clean-integer [])

(defn null-integer[value]
  (if (v/has-value? value)
    (if (or (v/valid-integer? value) (integer? value))
      (Integer. value)
      nil)
    nil))
