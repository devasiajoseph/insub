(ns libs.centipair.init
  (:require
   [libs.centipair.dashboard.navigation :as nav]
   [libs.centipair.components.notifier :as notifier]
   [libs.centipair.dashboard.channels :as c-chan]
   ))



(defn init-centipair
  []
  (do
    (.log js/console "Initializing centipair libs")
    (nav/hook-browser-navigation!)
    (notifier/render-notifier-component)
    (c-chan/load-auth)))
