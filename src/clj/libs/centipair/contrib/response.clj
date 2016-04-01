(ns libs.centipair.contrib.response
  (:require [ring.util.response :as ring-response]
            [cheshire.core :refer [generate-string]]
            [liberator.representation :refer [ring-response]]))


(defn redirect
  "Redirect function"
  [url]
  (ring-response/redirect url))

(defn status-code
  [status]
  (if (nil? status) 
    200 
    status))


(defn ring-response-format
  [status headers body]
  {:status (status-code status)
   :headers headers
   :body body})


(defn json-response
  [json-data & [status]]
  (ring-response-format status
                        {"Content-Type" "application/json"}
                        (generate-string json-data)))



(defn liberator-json-response
  [json-data & [status]]
  (ring-response (ring-response-format status
                        {"Content-Type" "application/json"}
                        (generate-string json-data))))


;; cookies format {"username" {:value "alice"} 
;;                 "some-key" {:value "some-value"}}
;; refer https://github.com/ring-clojure/ring/wiki/Cookies
(defn json-response-cookies
  [json-data cookies & [status]]
  {:status (status-code status)
   :headers {"Content-Type" "application/json"}
   :body (generate-string json-data)
   :cookies cookies})


(defn liberator-json-response-cookies
  [json-data cookies & [status]]
  (ring-response {:status (status-code status)
                  :headers {"Content-Type" "application/json"}
                  :body (generate-string json-data)
                  :cookies cookies}))
