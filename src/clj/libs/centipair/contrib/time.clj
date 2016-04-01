(ns libs.centipair.contrib.time
  (:require 
   [clj-time.core :as clj-time]
   [clj-time.local :as clj-time-local]
   [clj-time.coerce :as clj-time-coerce]
   [clj-time.format :as f]))


(defn set-time-expiry
  "gets a future time for expire time usage
  time is in hours"
  [hours]
  (clj-time-coerce/to-timestamp (clj-time/plus (clj-time-local/local-now) (clj-time/hours hours))))

(defn time-expired?
  "Check whether a given time has ben passed based on current time"
  [check-time]
  (let [session-time (clj-time-coerce/to-date-time check-time)
        expired? (clj-time/after? (clj-time-local/local-now) session-time)]
    expired?))

(defn expire-days
  "gets a future time for expire time usage
  time is in days"
  [days]
  (clj-time/plus (clj-time-local/local-now) (clj-time/days days)))

(defn set-expire-days
  "gets a future time for expire time usage
  time is in days"
  [days]
  (clj-time-coerce/to-sql-time (expire-days days)))


(defn cookie-expire-time
  "Sets cookies expire time for browsers
  uses rfc822 format"
  [days]
  (f/unparse (f/formatters :rfc822) (clj-time/plus (clj-time/now) (clj-time/days days))))

(defn sql-time-now
  "current time in sql format"
  []
  (clj-time-coerce/to-sql-time (clj-time/now)))

(defn highchart-date-format
  "date format used in highcharts charts"
  [pg-timestamp]
  (clj-time-coerce/to-long pg-timestamp))


(defn from-string-timestamp
  "Gets time from string timestamp"
  [s-timestamp]
  (clj-time-coerce/from-long (Long/parseLong s-timestamp)))


(defn to-sql-time
  "converts date time to sql format"
  [date-time]
  (clj-time-coerce/to-sql-time date-time))
