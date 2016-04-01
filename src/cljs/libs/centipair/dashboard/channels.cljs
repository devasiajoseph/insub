 (ns libs.centipair.dashboard.channels
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.core :as reagent]
            [cljs.core.async :refer [put! chan <! >!]]
            [libs.centipair.utilities.inajax :as ajax]
            [libs.centipair.utilities.spa :as spa]
            ))



(def auth-waiting-counter (atom 0))
(def auth-channel (chan))

(def navigation-channel (chan))
(def auth (atom {}))


(def dashboard-menu (atom nil))

(def backoffice-links (reagent/atom []))

(defn auth-function
  [f]
  (if (empty? @auth)
    (do
      (swap! auth-waiting-counter inc)
      (go
        (f (<! auth-channel))))
    (do
      (reset! auth-waiting-counter 0)
      (f @auth))))


(defn activate-navigation
  []
  (if (:logged-in @auth)
    (let [nav-menu @dashboard-menu]
      (go (>! navigation-channel nav-menu)))))


(defn load-auth
  []
  (ajax/bget-json
   "/api/centipair/user/status"
   nil
   (fn [response]
     (if (:logged-in response)
       (do
         (reset! auth response)
         (activate-navigation)
         (dotimes [n @auth-waiting-counter]
           (go (>! auth-channel @auth))))
       (spa/redirect "/login")))))
