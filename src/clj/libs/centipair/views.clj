(ns libs.centipair.views
  (:require [centipair.layout :as layout]))


(defn dashboard
  []
  (layout/render "sbadmin.html"))
