(ns centipair.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[centipair started successfully]=-"))
   :middleware identity})
