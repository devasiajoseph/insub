(ns libs.centipair.contrib.mail
  (:use postal.core
        selmer.parser)
  (:require [clojure.tools.logging :as log]
            [centipair.config :refer [env]]))

(defn send-registration-email [registration-request]
  (log/info "Sending registration email")
  (let [site-data (:site-settings env)
        email-body (render-file "centipair/email/registration.html" 
                                (assoc site-data :registration-key (str (:registration_key registration-request))))]
    (send-message (:email env)
                  {:from (:email site-data)
                   :to (:email registration-request)
                   :subject (str "Please activate your " (:name site-data) " account")
                   :body [{:type "text/html"
                           :content email-body}]})))

(defn send-password-reset-email [params]
  (log/info "Sending password reset email.")
  (let [site-data (:site-settings env)
        email-body (render-file "centipair/email/password-reset.html" 
                                (assoc site-data :reset-key (:password-reset-key params)))]
    (send-message (:email env)
                  {:from (:email site-data)
                   :to (:email params)
                   :subject (str "Reset your " (:name site-data) " password")
                   :body [{:type "text/html"
                           :content email-body}]})))


(defn send-test-email [mail]
  (log/info "Sending test email.")
  (send-message (:email env)
                {:from "test@centipair.com"
                 :to "devasiajosephtest@gmail.com"
                 :subject (str "testing email number - " (:body mail))
                 :body (str "test email number -" (:body mail) )}))


(defn send-mail
  "Format : {:purpose registration :}"
  [mail]
  (log/info "Sending mail")
  (case (:purpose mail)
    "registration" (send-registration-email (:params mail))
    "test" (send-test-email (:params mail))))
