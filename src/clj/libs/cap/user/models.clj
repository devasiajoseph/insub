(ns libs.cap.user.models
  (:require [libs.centipair.contrib.cryptography :as cry]
            [validateur.validation :refer :all]
            [libs.centipair.utilities.validators :as v]))


(def user-account-table "user_account")
(def user-login-username-table "user_login_username")
(def user-login-email-table "user_login_email")
(def user-session-table "user_session")
(def user-session-index-table "user_session_index")
(def user-account-registration-table "user_account_registration")
(def user-profile-table "user_profile")
(def password-reset-table "password_reset")


(defn get-user-username
  [username])


(defn get-user-email
  [email])


(defn get-user-session
  [session-key]
  )


(defn get-user
  [field value]
  (case field
    "username" (get-user-username value)
    "email" (get-user-email value)
    "session" (get-user-session value)))


(defn email-exist-check
  [email])

(defn username-exist-check
  [username])

;;Registration workflow

(def registration-validator
  (validation-set
   (presence-of :email :message "Your email address is required for registration")
   (presence-of :username :message "Please select a username for your profile")
   (presence-of :password :message "Please choose a password")
   (validate-by :email email-exist-check :message "This email already exists")
   (validate-by :username username-exist-check :message "This username already exists")))


(defn validate-user-registration
  [params]
  (let [validation-result (registration-validator params)]
    (if (valid? validation-result)
      true
      [false {:validation-result {:errors validation-result}}])))



(defn activate-user
  [params])


(defn create-user
  [params])


(defn create-registration-request
  [params])


(defn validate-user-registration
  [params])

(defn register-user
  [params]
  
  )


;;Registration workflow ends


(defn login-user
  [params])


(defn forget-password
  [params])


(defn reset-password
  [params])
