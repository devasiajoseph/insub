(ns apps.init
  (:require [apps.wemebox.init :as wemebox-init]
            [libs.centipair.init :refer [init-centipair]]))

(defn init!
  []
  (do
    (init-centipair)
    (wemebox-init/init!)
    )
  (.log js/console "Clojurescript Apps initialized"))
