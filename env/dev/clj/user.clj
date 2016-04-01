(ns user
  (:require [mount.core :as mount]
            centipair.core))

(defn start []
  (mount/start-without #'centipair.core/repl-server))

(defn stop []
  (mount/stop-except #'centipair.core/repl-server))

(defn restart []
  (stop)
  (start))


