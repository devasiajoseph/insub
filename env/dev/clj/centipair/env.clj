(ns centipair.env
  (:require [selmer.parser :as parser]
            [clojure.tools.logging :as log]
            [centipair.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[centipair started successfully using the development profile]=-"))
   :middleware wrap-dev})
