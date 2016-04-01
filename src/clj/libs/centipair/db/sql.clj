(ns libs.centipair.db.sql
  (:require [korma.core :as korma :refer [select where]]))




(defn find-by-field
  [entity]
  (fn [field value]
    (select entity (where {field value}))))


