(ns libs.centipair.urls
  (:require [compojure.core :refer [defroutes GET POST ANY]]            
            [libs.centipair.user.api :as user-api]
            [libs.centipair.user.routes :as user-routes]
            [libs.centipair.views :as c-views]))


(defroutes centipair-urls
  ;;(GET "/dashboard" [] (c-views/dashboard))
  (GET "/user/activate" request (user-routes/activate-user request))
  (GET "/logout" request (user-routes/logout request))
  ;;(ANY "/api/centipair/admin/user" [] (user-api/admin-api-user))
  ;;(ANY "/api/centipair/admin/user/search" [] (user-api/admin-api-user-search))
  ;;(ANY "/api/centipair/admin/user/:id" [id] (user-api/admin-api-user id))
  (GET "/api/centipair/user/status" [] (user-api/api-user-status))
  (POST "/api/centipair/register" [] (user-api/api-user-register))
  (POST "/api/centipair/login" [] (user-api/api-user-login)))
