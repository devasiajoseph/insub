(ns centipair.handler
  (:require [compojure.core :refer [routes wrap-routes]]
            [centipair.layout :refer [error-page]]
            [compojure.route :as route]
            [centipair.middleware :as middleware]
            [apps.urls :refer [app-urls]]
            [libs.centipair.urls :refer [centipair-urls]]
            ))

(def app-routes
  (routes
   (wrap-routes #'centipair-urls middleware/wrap-csrf)
   (wrap-routes #'app-urls middleware/wrap-csrf)
    (route/not-found
      (:body
        (error-page {:status 404
                     :title "page not found"})))))

(def app (middleware/wrap-base #'app-routes))
