(ns libs.centipair.user.models.interface
  (:require [validateur.validation :refer :all]
            [libs.centipair.user.models.cassandra :as user-models]
            [libs.centipair.async.channels :as channels]))



(defn unique-email?
  [email]
  (if (nil? (user-models/get-email email))
    true
    false))

(defn unique-username?
  [username]
  (if (nil? (user-models/get-username username))
    true
    false))

;;Registration workflow

(def registration-validator
  (validation-set
   (presence-of :email :message "Your email address is required for registration")
   (presence-of :username :message "Please select a username for your profile")
   (presence-of :password :message "Please choose a password")
   (validate-by :email unique-email? :message "This email already exists")
   (validate-by :username unique-username? :message "This username already exists")))


(defn validate-user-registration
  [params]
  (let [validation-result (registration-validator params)]
    (if (valid? validation-result)
      true
      [false {:validation-result {:errors validation-result}}])))


(def login-validator
  (validation-set
   (presence-of :username :message "Please enter your username or email")
   (presence-of :password :message "Please enter your password")))


(defn validate-login
  [params]
  (let [validation-result (login-validator params)]
    (if (valid? validation-result)
      (user-models/valid-login? params)
      [false {:validation-result {:errors validation-result}}])))



(defn register-user
  [params]
  (user-models/register-user params))


(defn activate-user
  [activation-key]
  (user-models/activate-user activation-key))


;;Registration workflow ends



(defn forget-password
  [params])


(defn reset-password
  [params])



(defn login
  [user]
  (user-models/login user))

(defn logout [request])


;;admin functions
(defn validate-admin-create-user
  [params] true)


(defn admin-save-user
  [params])


(defn admin-delete-user
  [user-account-id])


(defn get-all-users
  [params])

(defn search-user
  [q])


(defn get-user
  [user-account-id])


(defn user-status
  [request]
  (user-models/user-status))
