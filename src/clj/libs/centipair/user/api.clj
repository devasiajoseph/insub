(ns libs.centipair.user.api
   (:use compojure.core)
   (:require [liberator.core :refer [resource defresource]]
             [libs.centipair.contrib.response :as response]
             [libs.centipair.user.models :as user-models]))


;;(POST "/api/centipair/register" [] (user-api/api-user-register))
(defresource api-user-register []
  :available-media-types ["application/json"]
  :allowed-methods [:post]
  :processable? (fn [context]
                  (user-models/validate-user-registration (:params (:request context))))
  :handle-unprocessable-entity (fn [context]
                                 (:validation-result context))
  :post! (fn [context] (user-models/register-user (:params (:request context))))
  :handle-created (fn [context] (response/liberator-json-response {:register "success"})))


;;(POST "/api/centipair/login" [] (user-api/api-user-login))
(defresource api-user-login []
  :available-media-types ["application/json"]
  :allowed-methods [:post]
  :processable? (fn [context]
                  (user-models/check-login (:params (:request context))))
  :handle-unprocessable-entity (fn [context]
                                 (:validation-result context))
  :post! (fn [context]
           {:login-result (user-models/login (:params (:request context)))})
  :handle-created (fn [context]
                    (response/liberator-json-response-cookies 
                     (:login-result context)
                     {"auth-token" {:value (:auth-token (:login-result context))
                                    :max-age 86400
                                    :path "/"
                                    :http-only true}})))


;;(POST "/api/centipair/logout" [] (user-api/api-user-logout))
(defresource api-user-logout []
  :available-media-types ["application/json"]
  :allowed-methods [:post]
  
  :post! (fn [context]
           {:login-result (user-models/logout-user (:request context))})
  :handle-created (fn [context] {:result "success"}))



;;(ANY "/api/centipair/admin/user" [] (user-api/admin-api-user))
;;(ANY "/api/centipair/admin/user/:id" [id] (user-api/admin-api-user id))
(defresource admin-api-user [&[source]]
  :available-media-types ["application/json"]
  :allowed-methods [:post :get :delete :put]
  :processable? (fn [context] (if (= (:request-method (:request context)) :get)
                                true
                                (if (= (:request-method (:request context)) :delete)
                                  true
                                  (user-models/validate-admin-create-user (:params (:request context))))))
  ;;:exists? (fn [context] (if (nil? source) true (page-exists?  source)))
  :handle-unprocessable-entity (fn [context] (:validation-result context))
  :post! (fn [context]
           {:created (user-models/admin-save-user (:params (:request context)))})
  :handle-created (fn [context] (:created context))
  :delete! (fn [context]  (user-models/delete-user source))
  :delete-enacted? false
  :handle-ok (fn [context] (if (nil? source) 
                             (user-models/get-all-users (:params (:request context))) 
                             (user-models/get-user source))))

;;(ANY "/api/centipair/admin/user/search" [] (user-api/admin-api-user-search))
(defresource admin-api-user-search []
  :available-media-types ["application/json"]
  :allowed-methods [:get]
  :handle-ok (fn [context] (user-models/search-user (get-in context [:request :params :q]))))


;;(GET "/api/centipair/user/status" [] (user-api/api-user-status))
(defresource api-user-status []
  :available-media-types ["application/json"]
  :allowed-methods [:get]
  :handle-ok (fn [context] (user-models/user-status (:request context))))
