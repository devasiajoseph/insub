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

(defn get-db-connection
  []
  (cc/connect (get-in env [:db :cassandra :cluster])
              {:keyspace (get-in env [:db :cassandra :keyspace])}))

(defstate cassandra-conn :start (get-db-connection))


(defn dbcon
  "Use this function as connection in cassandra related functions"
  []
  cassandra-conn)
