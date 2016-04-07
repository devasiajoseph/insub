(ns libs.centipair.user.models.cassandra
  (:require [libs.centipair.db.connection :refer [dbcon]]
            [clojurewerkz.cassaforte.cql :as cql]
            [clojurewerkz.cassaforte.uuids :as c-uuid]
            [libs.centipair.contrib.cryptography :as crypto]
            [libs.centipair.contrib.time :as time]
            [libs.centipair.async.channels :as channels]
            [libs.centipair.contrib.cookies :as cookies]
            [clojure.tools.logging :as log]
            ))


(def user-account "user_account")
(def user-username "user_username")
(def user-email "user_email")
(def email-verification "email_verification")
(def user-session "user_session")
(def user-session-index"user_session_index")
(def user-account-registration-table "user_account_registration")
(def user-profile-table "user_profile")
(def password-reset-table "password_reset")


(defn get-user-id
  "user account id must be of type uuid"
  [user-account-id]
  (first (cql/select (dbcon) user-account (cql/where [[= :user_account_id user-account-id]]))))

(defn get-username
  [username]
  (first (cql/select (dbcon) user-username (cql/where [[= :username username]]))))



(defn get-email
  [email]
  (first (cql/select (dbcon) user-email (cql/where [[= :email email]]))))


(defn get-user-session
  [session-key]
  
  )


(defn get-user-username
  [username]
  (let [u-username (get-username username)]
    (if (not (nil? u-username))
      (get-user-id (:user_account_id u-username))
      nil)))


(defn get-user-email
  [email]
  (let [u-email (get-email email)]
    (if (not (nil? u-email))
      (get-user-id (:user_account_id u-email))
      nil)))



(defn get-user
  [field value]
  (case field
    "username" (get-user-username value)
    "email" (get-user-email value)
    "session" (get-user-session value)))


(defn get-authenticated-user
  [request]
  (let [auth-token (cookies/get-auth-token request)]
    
    ))


(defn email-exist?
  [email]
  (if (nil? (get-email email))
    false
    true))


(defn username-exist?
  [username]
  (if (nil? (get-username username))
    false
    true))

(defn user-exist?
  [params]
  (or (email-exist? (:email params)) (username-exist? (:username params))))

(defn username-exist-check
  [username])

;;Registration workflow


(defn get-email-verification
  [activation-key]
  (first (cql/select (dbcon) email-verification (cql/where [[:= :verification_key activation-key]]))))


(defn commit-user-activation
  [verification]
  (do 
    (cql/delete (dbcon) email-verification (cql/where [[:= :verification_key (:verification_key verification)]]))
    (cql/update (dbcon) user-account {:email_verified true :active  true} (cql/where [[:= :user_account_id (:user_account_id verification)]])))
  {:message "Your account has been activated"})

(defn activate-user
  [activation-key]
  (let [verification (get-email-verification activation-key)]
    (if (nil? verification)
      {:message "Invalid verification key"}
      (commit-user-activation verification))))


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
  [new-account]
  (let [vr-params {:verification_key (crypto/generate-key 16)
                   :user_account_id (:user_account_id new-account)
                   :created_date (time/unix-timestamp-now)
                   }]
    (do (cql/insert (dbcon) email-verification vr-params))
    vr-params))




(defn register-user
  [params]
  (if (user-exist? params)
    (log/warn "User already exists..Skipping")
    (let [new-account (create-user-account params)
        vr-params (create-registration-request new-account)]
      (do (add-username new-account)
          (add-email new-account)
          (channels/send-async-mail {:purpose "registration"
                                     :params {:email (:email params)
                                              :registration_key (:verification_key vr-params)}})))))


;;Registration workflow ends


(defn login-user
  [params])


(defn forget-password
  [params])


(defn reset-password
  [params])



(defn delete-user-account
  "provide email to delete a user account"
  [email]
  (let [uac (get-user-email email)]
    (if (not (nil? uac))
      (do 
        (cql/delete (dbcon) user-email (cql/where [[= :email email]]))
        (cql/delete (dbcon) user-username (cql/where [[= :username (:username uac)]]))
        (cql/delete (dbcon) user-account (cql/where [[= :user_account_id (:user_account_id uac)]]))))
    uac))
