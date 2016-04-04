(ns libs.centipair.user.models.interface
  (:require [libs.centipair.user.models.cassandra :as user-models]
            [libs.centipair.async.channels :as channels]))



(defn get-user-username
  [username]
  (first (user-models/get-user-username)))


(defn get-user-email
  [email]
  (first (user-models/get-user-email)))


(defn get-user-session
  [session-key])


(defn get-user
  [field value]
  (case field
    "username" (get-user-username value)
    "email" (get-user-email value)
    "session" (get-user-session value)))


(defn unique-email?
  [email]
  (if (nil? (get-user-email email))
    true
    false))

(defn unique-username?
  [username]
  (if (nil? (get-user-username username))
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



(defn activate-user
  [params]
  (user-models/activate-user params))



(defn create-registration-request
  [params]
  (user-models/create-registration-request params))


(defn register-user
  [params]
  (let [user-account (user-models/create-user-account params)]
    
    ))


;;Registration workflow ends


(defn login-user
  [params])


(defn forget-password
  [params])


(defn reset-password
  [params])
