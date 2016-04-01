(ns libs.centipair.contrib.cookies)


(defn get-auth-token [request]
  (get-in request [:cookies "auth-token" :value]))
