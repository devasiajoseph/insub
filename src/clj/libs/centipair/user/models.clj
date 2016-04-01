(ns libs.centipair.user.models
  (:use korma.db
        libs.centipair.db.connection
        libs.centipair.contrib.time
        libs.centipair.contrib.mail
        libs.centipair.utilities.pagination)
  (:require
   [korma.core :as korma :refer [insert
                                 delete
                                 select
                                 where
                                 set-fields
                                 values
                                 fields
                                 offset
                                 limit
                                 defentity
                                 pk
                                 has-many
                                 join
                                 with
                                 exec-raw
                                 ]]
   [validateur.validation :refer :all]
   [libs.centipair.utilities.validators :as v]
   [libs.centipair.contrib.cookies :as cookies]
   [libs.centipair.contrib.cryptography :as crypt]
   [libs.centipair.utilities.errors :as errors]
   [libs.centipair.async.channels :as channels]
   [libs.centipair.db.sql :refer [find-by-field]]
   ))



(defentity user_session)

(defentity user_account
  (pk :user_account_id)
  (has-many user_session))

(defentity user_profile)

(defentity user_registration)

(defentity user_password_reset)

(def find-user (find-by-field user_account))

(def login-error {:status-code 422 :errors {:username ["Email or Password is incorrect"]}})

(def user-inactive-error {:status-code 422 :errors {:username ["This account is inactive"]}})


(defn get-user-profile
  [user-id]
  (first (select user_profile (where {:user_account_id user-id}))))

(defn login-success
  "What happens after a successful login"
  [user-account auth-token]
  (if (:is_admin user-account)
    {:status-code 200 :message "success" :redirect "/admin" :auth-token auth-token :result {:loggedin true :profile (get-user-profile (:user_account_id user-account))}}
    {:status-code 200 :message "success" :redirect "/" :auth-token auth-token :result {:loggedin true :profile (get-user-profile (:user_account_id user-account))}}))


(def registration-success {:status-code 200 :message "Registration success"})

(defn select-user-email
  "Select user account based on email"
  [email]
  (first (select user_account (where {:email email}))))


(defn new-user-account
  "Creates new user account"
  [params]
  (insert user_account (values {:username (:email params)
                                :password (crypt/encrypt-password (:password params))
                                :email (:email params)
                                :created_on (sql-time-now)
                                :is_admin false
                                :active true ;;false if activation link required
                                })))


(defn create-registration-request
  "Creates registration request"
  [user-account]
  (insert user_registration (values {:user_account_id (:user_account_id user-account)
                                     :registration_key (crypt/uuid)})))


(defn register-user
  "User registration db operations"
  [params]
  (let [user-account (new-user-account params)
        registration-request (create-registration-request user-account)]
      (channels/send-async-mail {:purpose "registration"
                                 :params (assoc registration-request :email (:email user-account))})
      registration-success))


(defn admin-create-user [params]
  (insert user_account (values {:username (:email params)
                                :password (crypt/encrypt-password (:password params))
                                :email (:email params)
                                :created_on (sql-time-now)
                                :active (:active params)
                                :is_admin (:is-admin params)})))

(defn user-update-fields [params]
  (let [fields {:username (:email params)
                :email (:email params)
                :created_on (sql-time-now)
                :active (:active params)
                :is_admin (:is-admin params)}]
    (if (not (nil? (:password params)))
      (assoc fields :password (crypt/encrypt-password (:password params)))
      fields)))

(defn admin-update-user [params]
  (korma/update user_account 
          (set-fields (user-update-fields params))
          (where {:user_account_id (Integer. (:user-id params))}))
  {:user_account_id (Integer. (:user-id params))})



(defn create-user-session
  "Creating user session for user account"
  [user-account]
  (let [auth-token (crypt/random-base64 48)]
    (do
      (insert user_session (values {:user_account_id (:user_account_id user-account)
                                    :auth_token auth-token 
                                    :session_expiry (set-time-expiry 23)}))
      (login-success user-account auth-token))))


(defn check-login [params]
  (let [user-account (select-user-email (:email params))]
    (if (nil? user-account)
      (errors/validation-error :username "User account not found")
      (if (not (:active user-account))
        (errors/validation-error :username "This account is inactive")
        (if (crypt/check-password (:password params) (:password user-account))
          true
          (errors/validation-error :password "Password is wrong"))))))

(defn login
  "Login DB operations"
  [params]
  (let [user-account (select-user-email (:email params))]
    (create-user-session user-account)))


(defn get-user-session
  "Gets user session based on auth-token"
  [auth-token]
  (first (select user_session (where {:auth_token auth-token}))))


(defn delete-session [auth-token]
  (do
    (delete user_session (where {:auth_token auth-token})))
  {:status-code 301})


(defn logout-user
  [request]
  (let [auth-token (cookies/get-auth-token request)]
    (if (nil? auth-token)
      {:status-code 301}
      (delete-session auth-token))))


(defn activate-account
  "Activates user account based on registration key"
  [registration-key]
  (let [user-registration (first (select user_registration (where {:registration_key registration-key})))
        user-id (:user_account_id user-registration)]
    (if (nil? user-id)
      false
      (do 
        (transaction
         (korma/update user_account (set-fields {:active true}) (where {:user_account_id user-id}))
         (korma/update user_registration (set-fields {:activated true})))
        true))))


(defn password-reset-email
  "Password reset save and send email"
  [params]
  (let [email (:email params)
        user-account (select-user-email email)]
    (if (nil? user-account)
      {:status-code 422 :errors {:email ["This email is not registered"]}}
      (do
        (let [password-reset-key (crypt/random-base64 8)
              password-reset (insert user_password_reset 
                                     (values 
                                      {:user_account_id (:user_account_id user-account)
                                       :password_reset_key password-reset-key
                                       :key_expiry (set-time-expiry 48)}))]
          (future (send-password-reset-email email password-reset-key)))
        
        {:status-code 200}))))


(defn get-password-reset
  "get password reset data"
  [password-reset-key]
  (first (select user_password_reset (where {:password_reset_key password-reset-key}))))


(defn valid-password-reset-key?
  "Check whether valid password reset key"
  [password-reset-key]
  (let [password-reset (get-password-reset password-reset-key)]
    (if (nil? password-reset)
      false
      (if (time-expired? (:key_expiry password-reset))
        false
        true))))



(defn update-password
  [params]
  (korma/update user_account 
                (set-fields
                 {:password (crypt/encrypt-password (:password params))}) 
                (where {:user_account_id (:user-id params)})))


(defn commit-reset-password
  "Save password reset"
  [params]
  (let [password-reset (get-password-reset (:password-reset-key params))]
    (if (nil? password-reset)
      {:status-code 422 :message "Invalid key"}
      (do
        (korma/update user_account 
                (set-fields
                 {:password (crypt/encrypt-password (:password params))}) 
                (where {:user_account_id (:user_account_id password-reset)}))
        {:status-code 200}))))


(defn get-user-session-account
  "gets user account from session"
  [auth-token]    
  (select user_account
          (fields :user_account_id :username :active :is_admin :user_session.session_expiry)
          (with user_session)
          (join :inner user_session (= :user_session.user_account_id :user_account_id))
          (where {:user_session.auth_token auth-token})))


(defn valid-session? [user-session-account]
  (if (time-expired? (:session_expiry user-session-account))
    false
    true))


(defn get-user-session
  "Gets the session data for current logged in user"
  [auth-token]
  (if (nil? auth-token)
      nil
      (let [user-session-account (first (get-user-session-account auth-token))]
        (if (valid-session? user-session-account)
          user-session-account
          nil))))


(defn delete-user
  "deletes user based on id
  used only for admin purpose"
  [user-id]
  (delete user_account (where {:user_account_id (Integer. user-id)})))

(defn get-all-users
  "fetches all user"
  [params]
  (let [offset-limit-params (offset-limit (:page params) (:per params))
        total (count (select user_account (fields :user_account_id)))]
    {:result (select user_account (fields :email :active :is_admin :user_account_id)
                     (offset (:offset offset-limit-params))
                     (limit (:limit offset-limit-params)))
     :total total
     :page (if (nil? (:page params)) 0 (Integer. (:page params)))}))


(defn get-user
  "Gets user based on id"
  [user-id]
  (first (select user_account 
                 (fields :email :active :is_admin :user_account_id)
                 (where {:user_account_id (Integer. user-id)}))))


(defn create-fb-user-profile
  [fb-account]
  (let [user-profile (get-user-profile (:user_account_id fb-account))]
    (if (nil? user-profile)
      (insert user_profile (values {:user_account_id (:user_account_id fb-account)
                                    :full_name (:facebook_name fb-account)}))
      user-profile)))


(defn search-user
  [query]
  (if (nil? query)
    []
    (exec-raw ["SELECT email FROM  user_account WHERE email ILIKE ?" [(str "%" query "%")]] :results)))



(defn admin-save-user
  [params]
  (let [result (if (nil? (:user-id params))
                 (admin-create-user params)
                 (admin-update-user params))]
    {:user_account_id (:user_account_id result)}))


(defn get-authenticated-user
  [request]
  (let [auth-token (cookies/get-auth-token request)]
    (if (nil? auth-token)
      nil
      (get-user-session auth-token))))


(defn logged-in?
  [request]
  (if (nil? (get-authenticated-user request))
    false
    true))


(defn user-status
  [request]
  (let [user-account (get-authenticated-user request)
        user-profile (if (not (nil? user-account))
                           (get-user-profile (:user_account_id user-account))
                           nil)]
    {:first-name (:first_name user-profile)
     :middle-name (:middle_name user-profile)
     :last-name (:last_name user-profile)
     :logged-in (not (nil? (:active user-account)))
     :admin (and (not (nil? user-account)) (:is_admin user-account))
     }))


(defn is-admin?
  [request]
  (let [auth-user (get-authenticated-user request)]
    (if (nil? auth-user)
      false
      (:is_admin auth-user))))

(defn is-admin-id?
  [user-id]
  (let [user-account (get-user user-id)]
    (:is_admin user-account)))

;;validators

(defn email-exist-check
  [value]
  (if (v/has-value? value)
    (if (nil? (select-user-email value))
      true
      false)))


(def registration-validator
  (validation-set
   (presence-of :email :message "Your email address is required for registration")
   (presence-of :password :message "Please choose a password")
   (validate-by :email email-exist-check :message "This email already exists")))




(defn unique-email-validator [params]
  (let [user-account (select-user-email (:email params))]
    (if (nil? user-account)
      true
      (if (nil? (:user-id params))
        [false {:validation-result {:errors {:email ["This email already exists"]}}}]
        (if (= (:user_account_id user-account) (Integer. (:user-id params)))
          true
          [false {:validation-result {:errors {:email ["This email already exists"]}}}])))))


(def admin-create-user-validator
  (validation-set
   (presence-of :email :message "Your email address is required for registration")))


(defn validate-admin-create-user
  [params]
  (let [validation-result (admin-create-user-validator params)]
    (if (valid? validation-result)
      (unique-email-validator params)
      [false {:validation-result {:errors validation-result}}])))


(defn validate-user-registration
  [params]
  (let [validation-result (registration-validator params)]
    (if (valid? validation-result)
      true
      [false {:validation-result {:errors validation-result}}])))


(def login-validator
  (validation-set
   (presence-of :username :message "Please enter the email address you have registered.")
   (presence-of :password :message "Please enter your password")))


(defn validate-user-login
  [params]
  (let [validation-result (login-validator params)]
    (if (valid? validation-result)
      true
      [false {:validation-result {:errors validation-result}}])))
