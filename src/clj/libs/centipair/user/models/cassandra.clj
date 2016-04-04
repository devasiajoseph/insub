(ns libs.centipair.user.models.cassandra
  (:require [libs.centipair.db.connection :refer [dbcon]]
            [clojurewerkz.cassaforte.cql :as cql]
            [clojurewerkz.cassaforte.uuids :as c-uuid]
            [libs.centipair.contrib.cryptography :as crypto]
            [libs.centipair.contrib.time :as time]
            ))


(def user-account "user_account")
(def user-username "user_username")
(def user-email "user_email")
(def user-session "user_session")
(def user-session-index"user_session_index")
(def user-account-registration-table "user_account_registration")
(def user-profile-table "user_profile")
(def password-reset-table "password_reset")


(defn get-user-username
  [username]
  (cql/select (dbcon) user-username (cql/where [[= :username username]])))


(defn get-user-email
  [email]
  (cql/select (dbcon) user-email (cql/where [[= :email email]])))


(defn get-user-session
  [session-key]

  )


(defn get-user
  [field value]
  (case field
    "username" (get-user-username value)
    "email" (get-user-email value)
    "session" (get-user-session value)))


(defn email-exist?
  [email]
  (if (nil? (get-user-email email))
    false
    true))

(defn username-exist-check
  [username])

;;Registration workflow




(defn activate-user
  [params])


(defn create-user-account
  [params]
  (let [ua-params {:user_account_id (c-uuid/time-based)
                   :username (:username params)
                   :password (crypto/encrypt-password (:password params))
                   :email (:email params)
                   :active (:active params)
                   :email_verified (:email_verified params)
                   :profile_verified (:profile_verified params)
                   :created_date (time/unix-timestamp-now)}]
    (do 
      (cql/insert (dbcon) user-account ua-params))
    ua-params))

(defn add-username
  [params]
  (cql/insert (dbcon) user-username {:username (:username params)
                                     :user_account_id (:user_account_id params)}))

(defn add-email
  [params]
  (cql/insert (dbcon) user-email {:email (:email params)
                                  :user_account_id (:user_account_id params)}))


(defn create-registration-request
  [params]
  
  )




(defn register-user
  [params]
  (let [new-account (create-user-account params)]
    (do (add-username new-account)
        (add-email new-account)
        (create-registration-request new-account))))


;;Registration workflow ends


(defn login-user
  [params])


(defn forget-password
  [params])


(defn reset-password
  [params])

