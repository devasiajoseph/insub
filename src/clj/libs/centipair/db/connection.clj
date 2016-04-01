(ns libs.centipair.db.connection
  (:use korma.db)
  (:require [centipair.config :refer [env]]
            [mount.core :refer [defstate]]
            [clojurewerkz.cassaforte.client :as cc]
            [clojurewerkz.cassaforte.cql    :as cql]))


(defn set-default-connection
  []
  (do
    (default-connection (create-db (postgres (get-in env [:db :sql])))))
  true)


(defstate sql-db-status :start (set-default-connection))


;;(defdb db (postgres {:db "accrue" :user "devasia" :password ""}))

