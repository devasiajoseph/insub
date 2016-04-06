(ns libs.centipair.user.routes
  (:require [compojure.core :refer :all]
            [centipair.layout :as layout]
            [libs.centipair.user.models.interface :as user-model]
            [ring.util.response :refer [redirect]]))


(defn logout [request]
  (user-model/logout request)
  (redirect "/"))


(defn activate-user
  [key]
  (let [activation-result (user-model/activate-user key)]
    (if (:result activation-result)
      "Activated"
      "Something wrong"
      )
    ))
