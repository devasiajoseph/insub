(ns apps.wemebox.routes
  (:require [secretary.core :as secretary :refer-macros [defroute]]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [apps.wemebox.user :as user])
  (:import goog.History))

(secretary/set-config! :prefix "#")

;; /#/
(defroute home "/" []
  (js/console.log "You are home"))

;; /#/boxes
(defroute boxes "/boxes" []
  (js/console.log "boxes"))

;; /#/settings
(defroute settings "/settings" []
  (js/console.log "settings"))


(defroute login "/login" []
  (js/console.log "Lopgin")
  (user/render-login-form))


(defroute signup "/signup" []
  (user/render-register-form))
