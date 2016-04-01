(ns apps.urls
  (:require [compojure.core :refer [defroutes GET POST ANY]]
            [apps.myapp.views :as my-views]))

;;Import urls from your apps here

(defroutes app-urls
  (GET "/" [] (my-views/home-page)))
