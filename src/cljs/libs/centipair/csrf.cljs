(ns libs.centipair.csrf
  (:require [libs.centipair.utilities.ajax :as ajax]
            [libs.centipair.utilities.dom :as dom]))


(defn fetch-csrf-token []
  (ajax/get-json "/csrf" {} 
                 (fn [response]
                   (dom/set-value "__anti-forgery-token" (:token response)))))
