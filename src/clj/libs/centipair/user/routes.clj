(ns libs.centipair.user.routes
  (:require [compojure.core :refer :all]
            [centipair.layout :as layout]
            [libs.centipair.user.models :as user-model]
            [ring.util.response :refer [redirect]]))


(defn logout [request]
  (user-model/logout-user request)
  (redirect "/"))
