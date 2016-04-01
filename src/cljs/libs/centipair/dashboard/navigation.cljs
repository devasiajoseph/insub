(ns libs.centipair.dashboard.navigation
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.core :as reagent]
            [libs.centipair.ui :as ui]
            [libs.centipair.components.notifier :as notify]
            [libs.centipair.dashboard.channels :as d-chan]
            [libs.centipair.user.forms :as user-forms]
            [libs.centipair.backoffice.views :as bo-views]
            [secretary.core :as secretary :refer-macros [defroute]]
            [cljs.core.async :refer [put! chan <! >!]]
            [goog.events :as events]
            [goog.history.EventType :as HistoryEventType])
  (:import goog.History))



;;menu item format {:label "Dashboard" :url "/dashboard" :id "dashboard" :active false}
(def admin-menu (reagent/atom nil))

(def admin-only-menu (reagent/atom nil))


(defn single-menu-component [each]
  [:li  {:key (str "admin-menu-list-" (:id each)) :class (if (:active each) "active" "")}
       [:a {:href (str "#" (:url each)) :key (str "admin-menu-link-" (:id each))}
        (:label each)]])


(defn parent-menu-component [each]
  [:li {:key (str "admin-menu-list-" (:id each)) :class (if (:active each) "active" "")}
   :a {:href "javascript:;"
       :data-toggle "collapse"
       :data-target (str "#" (:id each) "-children")
       :key (str "admin-menu-link-" (:id each))}
   [:i {:class (str "fa fa-fw fa-" (:icon each))}] " "
   (:label each)

   [:ul {:id (str (:id each) "-children")
         :class "collapse"
         :key (str (:id each) "-children")}
    (map single-menu-component (:children each))]])


(defn menu-item [each]
  (if (:children each)
    (parent-menu-component each)
    (single-menu-component each)))

(defn menu-component []
  [:ul {:class "nav navbar-nav side-nav" ;;old class: nav nav-sidebar 
        :key (str "admin-menu-container" )}
   (doall 
    (map menu-item @admin-menu))])

(defn deactivate [id item]
  (if (:children item)
        (assoc item :children (map (partial deactivate id) (:children item)))
        (if (= id (:id item))
          (do
            ;;setting active-page here
            (assoc item :active true))
          (assoc item :active false))))


(defn authenicate-menu-activation
  [id auth]
  (do (notify/notify 200)
      (if (:logged-in auth)
        (reset! admin-menu (into [] (map (partial deactivate id) @admin-menu)))
        )))

(defn activate-side-menu-item [id]
  (d-chan/auth-function (partial authenicate-menu-activation id)))


(defn render-admin-menu []
  (ui/render menu-component "dashboard-menu"))


(def backoffice-menu-links [{:label "Back office" :id "backoffice" :url "/backoffice" :active false}])
(defn authenticate-dashboard-navigation
  [app-menu auth]
  (if (:logged-in auth)
    (if (:admin auth)
      (do
        (reset! admin-menu (concat app-menu backoffice-menu-links))
        (render-admin-menu))
      (do
        (reset! admin-menu app-menu)
        (render-admin-menu)))
    (do
      (reset! admin-menu []))))



(defn render-dashboard-navigation [app-menu]
  (d-chan/auth-function (partial authenticate-dashboard-navigation app-menu)))


(defn set-dashboard-navigation
  [app-menu]
  (reset! d-chan/dashboard-menu app-menu))


(defn set-backoffice-navigation
  [bo-links]
  (reset! d-chan/backoffice-links bo-links))


(defn prepare-navigation
  []
  (go (while true
        (render-dashboard-navigation (<! d-chan/navigation-channel)))))





;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (prepare-navigation)
  (doto (History.)
        (events/listen
          HistoryEventType/NAVIGATE
          (fn [event]
              (secretary/dispatch! (.-token event))))
        (.setEnabled true)))


;; Quick and dirty history configuration.
;;(let [h (History.)] (goog.events/listen h EventType/NAVIGATE #(secretary/dispatch! (.-token %))) (doto h (.setEnabled true)))


