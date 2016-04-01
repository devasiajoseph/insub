(ns apps.myapp.views
  (:require [centipair.layout :as layout]))


(defn home-page []
  (layout/render "home.html"))

