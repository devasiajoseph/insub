(ns libs.centipair.components.pure.input
  (:require [libs.centipair.components.notifier :as notifier]))



(defn make-valid
  [field]
  (swap! field assoc :message "" :class-name "pure-control-group")
  true)


(defn make-invalid
  [field result]
  (swap! field assoc
                 :message (:message result)
                 :class-name "pure-control-group form-error"))

(defn valid-field?
  [field]
  (if (nil? (:validator @field))
    (do 
      (make-valid field)
      true)
    (let [result ((:validator @field) @field)]
      (if (:valid result)
        (make-valid field)
        (do
          (make-invalid field result)
          false)))))

(defn valid-form?
  [form-fields]
  (apply = true (doall (map valid-field? form-fields))))



(defn update-value
  [field value]  
  (swap! field assoc :value value))

(defn update-check
  [field checked?]
  (if checked?
    (reset! field (assoc @field :checked "checked"))
    (reset! field (assoc @field :checked ""))))


(defn update-select-text [key field value]
  (let [dim (key @field)]
    (reset! field (assoc @field key (assoc dim :value value)))))


(defn text
  [field]
  [:div {:class (if (nil? (:class-name @field)) "pure-control-group" (:class-name @field))
         :key (str "control-group-container-" (:id @field))}
   [:label {:for (:id @field) :key (str "label-" (:id @field))} (:label @field)]
   [:input {:id (:id @field)
            :type (:type @field)
            :key (str "input-type-text-" (:id @field))
            :placeholder  (if (nil? (:placeholder @field))
                            ""
                            (:placeholder @field))
            :value (:value @field)
            :on-change #(update-value field (-> % .-target .-value) )
            :disabled (if (:disabled @field) "disabled" "")}]
   [:div {:class "input-message" :key (str "input-message-" (:id @field))} (:message @field)]])


(defn checkbox
  [field]
  [:div {:class (if (nil? (:class-name @field)) "pure-control-group" (:class-name @field))
         :key (str "control-group-container-" (:id @field))}
   [:label {:for (:id @field) :key (str "label-" (:id @field))} (:label @field)]
   [:input {:type (:type @field) :id (:id @field)
             :value (:value @field)
             :on-change #(do
                           (update-check field (-> % .-target .-checked))
                           (if (not (nil? (:on-change @field))) ((:on-change @field) field)))
             :checked (:checked @field)
             :key (str "key-" (:id @field))
            :disabled (if (:disabled @field) "disabled" "")}]
   [:span {:key (str "description-" (:id @field))} (:description @field)]
   [:div {:class "input-message" :key (str "input-message-" (:id @field))} (:message @field)]])




(defn perform-action
  [form action form-fields]
  (println "[erform action")
  (if (valid-form? form-fields)
    (do
      (swap! form assoc :message "")
      (action))
    (do
      ;;(swap! form assoc :message "Invalid data!")
      (notifier/notify 422 "Invalid data submitted"))))

(defn button-field
  [form form-fields action-button]
  [:button {:type "button"
            :class "pure-button pure-button-primary"
            :on-click #(perform-action form (:on-click action-button) form-fields)
            :disabled (if (nil? (:disabled action-button)) "" "disabled")
            :key (:id action-button)}
   (:label action-button)])


(defn button
  [form form-fields action-button]
  [:div {:class "pure-controls"}
   (if (= (:type @action-button) "button-group")
      (doall (map (partial button-field form form-fields) (:buttons @action-button)))
      (button-field form form-fields @action-button))])


(defn input-field [field]
  (case (:type @field)
    "text" (text field)
    "email" (text field)
    "password" (text field)
    "checkbox" (checkbox field)))


(defn form-aligned
  [form form-fields action-button]
  
  [:div {:class "panel"}
   [:form {:class "pure-form  pure-form-aligned form-weme"}
    [:legend [:h3 (:title @form)] [:span {:class "form-error"} (:message @form)]]
    (doall (map input-field form-fields))
    (button form form-fields action-button)]])




(defn append-error
  [errors field]
  
  )
