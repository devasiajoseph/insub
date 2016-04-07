(ns apps.wemebox.user
  (:require [libs.centipair.components.pure.input :as input]
            [libs.centipair.utilities.validators :as v]
            [libs.centipair.utilities.dom :as dom]
            [libs.centipair.ui :as ui]
            [libs.centipair.components.notifier :as notifier]
            [libs.centipair.utilities.ajax :as ajax]
            [reagent.core :as reagent]))


(def registration-form-state (reagent/atom {:title "Sign Up" :action "/register-submit" :id "registration-form"}))
(def username (reagent/atom {:id "username" :type "text" :label "Username" :validator v/required } ))
(def email (reagent/atom {:id "email" :type "email" :label "Email" :validator v/email-required} ))
(def password (reagent/atom {:id "password" :type "password" :label "Password" :validator v/required}))
(def accept-box-terms (reagent/atom {:id "box-terms" :type "checkbox" :label "Terms & Conditions" :validator v/agree-terms :description "I've read and accept terms & conditions"}))

(defn password-required-match [field]
  (if (v/has-value? (:value field))
    (if (= (:value field) (:value @password))
      (v/validation-success)
      (v/validation-error "Passwords does not match"))
    (v/validation-error v/required-field-error)))

(def confirm-password (reagent/atom {:id "confirm-password" :type "password" :label "Confirm Password" :validator password-required-match}))


(defn registration-success-ui
  []
  [:div {:class "panel"} [:h4 "Registration success"]
   [:p "Please click on the activation link in your email to activate your account."]])

(defn register-submit []
  (ajax/form-post
   registration-form-state
   "/api/centipair/register"
   [username email password confirm-password]
   (fn [response]
     (ui/render-ui registration-success-ui "content"))))

(def register-submit-button (reagent/atom {:label "Submit" :on-click register-submit :id "register-submit-button"}))

(defn registration-form []
  (input/form-aligned  
   registration-form-state
   [username
    email
    password
    confirm-password
    accept-box-terms]
   register-submit-button))


(defn render-register-form []
  (ui/render-ui registration-form "content"))



(def login-email (reagent/atom {:id "email" :type "email" :label "Email/Username" :validator v/email-required} ))
(def login-password (reagent/atom {:id "password" :type "password" :label "Password" :validator v/required}))
(def login-form-state (reagent/atom {:title "Login" :action "/login-submit" :id "login-form"}))


(defn login-submit []
  (ajax/form-post
   login-form-state
   "/api/centipair/login"
   [login-email login-password]
   (fn [response] 
     (.replace js/window.location (:redirect response)))))

(def login-submit-button (reagent/atom {:label "Submit" :on-click login-submit :id "login-submit-button"}))

(defn login-form []
  (input/form-aligned login-form-state [login-email login-password] login-submit-button))

(defn render-login-form []
  (println "render-login-form")
  (ui/render-ui login-form "content"))

