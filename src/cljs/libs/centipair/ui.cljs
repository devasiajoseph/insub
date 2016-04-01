(ns libs.centipair.ui
  (:require
   [reagent.core :as reagent :refer [atom]]
   [libs.centipair.dashboard.channels :as d-chan]))



(defn render-ui
  [elements root-id]
  (reagent/render
   [elements] 
   (. js/document (getElementById root-id))))


(defn unauthorized-view
  []
  [:h1 "Unauthorized"])

(defn ui-authorized?
  [permission-key auth]
  (if (nil? permission-key)
    (:logged-in auth)
    (permission-key auth)))

(defn auth-render
  [elements root-id permission-key auth]
  (if (ui-authorized? permission-key auth)
    (render-ui elements root-id)
    (render-ui unauthorized-view root-id)))


(defn render
  [elements root-id &[permission-key]]
  (d-chan/auth-function (partial auth-render elements root-id permission-key)))



(defn cleared-element []
  [:span])

(defn clear [root-id]
  (reagent/render 
   [cleared-element] 
   (. js/document (getElementById root-id))))


(defn render-page-message [message]
  message)


;;page : {:title "title" :action-bar (define action bar from libs.centipair.components.action) }
(defn define-page [page elements]
  [:div {:id "page-container"}
   [:h3 {:id "page-title"} (:title @page)]
   [:div {:id "page-message"} (render-page-message (:message @page))]
   [:div {:id "action-bar"} 
    (if (nil? (:action-bar @page))
      ""
      ((:action-bar @page)))]
   (elements)])

(defn render-page [page elements]
  (render (partial define-page page elements) "content"))
