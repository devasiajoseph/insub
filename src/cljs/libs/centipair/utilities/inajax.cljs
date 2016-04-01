;;This serves to avoid circular dependency for input components
(ns libs.centipair.utilities.inajax
  (:require [ajax.core :refer [GET POST DELETE json-request-format json-response-format]]
            [libs.centipair.utilities.dom :as dom]
            [reagent.core :as reagent]))


(defn bget-json
  [url params function-handler]
  (GET url
       :params params
       :handler function-handler
       :error-handler (fn [response] (.log js/console "Error!"))
       :format (json-request-format)
       :response-format (json-response-format {:keywords? true})))

