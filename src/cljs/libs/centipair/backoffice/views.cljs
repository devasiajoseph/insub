(ns libs.centipair.backoffice.views
  (:require [libs.centipair.ui :as ui]
            [libs.centipair.dashboard.channels :as d-chan]
            ))



(defn backoffice-home-ui
  []
  [:div 
   [:h1 "Back Office"]
   [:div {:class "list-group"}
    (map (fn [item]
           [:a {:href (:url item) :class "list-group-item" :key (str "list-item-" (:id item))} (:label item)])
         @d-chan/backoffice-links)]])


(defn backoffice-home-view
  []
  (ui/render backoffice-home-ui "content" :admin))
