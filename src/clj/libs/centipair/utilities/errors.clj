(ns libs.centipair.utilities.errors)


(defn validation-error
  "Global validation error format"
  [key message]
  [false {:validation-result {:errors {key message}}}])
