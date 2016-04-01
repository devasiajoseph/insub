(ns libs.centipair.user.forms
  (:require [libs.centipair.components.input :as input]
            [libs.centipair.utilities.validators :as v]
            [libs.centipair.utilities.dom :as dom]
            [libs.centipair.ui :as ui]
            [libs.centipair.components.notifier :as notifier]
            [libs.centipair.utilities.ajax :as ajax]
            [reagent.core :as reagent :refer [atom]]
            [libs.centipair.dashboard.channels :as d-chan]
            [libs.centipair.utilities.spa :as spa]
            ))



(def registration-form-state (atom {:title "Sign Up" :action "/register-submit" :id "registration-form"}))
(def register-email (atom {:id "email" :type "email" :label "Email" :validator v/email-required} ))

(def register-password (atom {:id "password" :type "password" :label "Password" :validator v/required}))

(def login-email (atom {:id "email" :type "email" :label "Email" :validator v/email-required} ))
(def forgot-email (atom {:id "email" :type "email" :label "Email" :validator v/email-required} ))

(def login-password (atom {:id "password" :type "password" :label "Password" :validator v/required}))
(def accept-box-terms (atom {:id "box-terms" :type "checkbox" :label "Terms & Conditions" :validator v/agree-terms :description "I've read and accept terms & conditions"}))

(defn password-required-match [field]
  (if (v/has-value? (:value field))
    (if (= (:value field) (:value @register-password))
      (v/validation-success)
      (v/validation-error "Passwords does not match"))
    (v/validation-error v/required-field-error)))






(def confirm-password (atom {:id "confirm-password" :type "password" :label "Confirm Password" :validator password-required-match}))

(def new-password (atom {:id "password" :type "password" :label "Confirm Password" :validator password-required-match}))


(defn new-password-required-match [field]
  (if (v/has-value? (:value field))
    (if (= (:value field) (:value @new-password))
      (v/validation-success)
      (v/validation-error "Passwords does not match"))
    (v/validation-error v/required-field-error)))

(def new-confirm-password (atom {:id "confirm-password" :type "password" :label "Confirm Password" :validator password-required-match}))

(defn register-submit []
  (ajax/form-post
   registration-form-state
   "/api/centipair/register"
   [register-email register-password confirm-password]
   (fn [response] (.log js.console "yay!!!"))))

(def register-submit-button (atom {:label "Submit" :on-click register-submit :id "register-submit-button"}))

(defn registration-form []
  [:div (input/form-aligned
         registration-form-state
         [register-email register-password confirm-password accept-box-terms]
         register-submit-button)
   [:div "Already registered?" [:a {:href "#/login"} "Login"]]])


(defn render-register-form []
  (ui/render-ui registration-form "content"))


(def login-form-state (atom {:title "Login" :action "/login-submit" :id "login-form"}))


(defn login-submit []
  (ajax/form-post
   login-form-state
   "/api/centipair/login"
   [login-email login-password]
   (fn [response]
     (do 
       (d-chan/load-auth)
       (spa/redirect "/")
       ))))

(def login-submit-button (atom {:label "Submit" :on-click login-submit :id "login-submit-button"}))

(defn login-form []
  [:div (input/form-aligned login-form-state [login-email login-password] login-submit-button)
   [:div "New account?" [:a {:href "#/register"} "Register"]]])

(defn render-login-form []
  (ui/render-ui login-form "content"))




(def forgot-password-subheading (atom {:id "forgot-password-subheading" :label "Enter your email to reset password" :type "subheading"}))

(def forgot-password-form-state (atom {:title "Forgot password" :action "/login-submit" :id "forgot-password-form"}))


(defn forgot-password-success []
  (notifier/message "Please check your email for instructions on resetting password" "success"))

(defn render-forgot-password-success []
  (ui/render-ui forgot-password-success "forgot-password-form"))


(defn forgot-password-submit
  []
  (ajax/form-post
   forgot-password-form-state
   "/forgot-password-submit"
   [forgot-email]
   (fn [response]
     (render-forgot-password-success))))

(def forgot-password-button (atom {:label "Submit" :on-click forgot-password-submit :id "forgot-password-button"}))

(defn forgot-password-form []
  (input/form-aligned
   forgot-password-form-state
   [forgot-password-subheading forgot-email]
   forgot-password-button))


(defn render-forgot-password-form []
  (ui/render-ui forgot-password-form "forgot-password-form"))


(def password-reset-key (atom {:id "password-reset-key" :type "text" :value ""}))
(def reset-password-form-state (atom {:id "reset-password-form" :title "Reset password"}))

(defn reset-password-submit
  []
  (swap! password-reset-key assoc :value (dom/get-value "password-reset-key"))
  (ajax/form-post
   reset-password-form-state
   "/reset-password-submit"
   [new-password new-confirm-password password-reset-key]
   (fn [response]
     (.log js/console (clj->js response)))))

(def reset-password-button (atom {:id "reset-password-button" :type "button" :label "Submit" :on-click reset-password-submit}))

(defn reset-password-form []
  (input/form-aligned
   reset-password-form-state
   [new-password new-confirm-password]
   reset-password-button))

(defn render-reset-password-form []
  (ui/render-ui reset-password-form "login-form"))
