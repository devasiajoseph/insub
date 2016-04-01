(ns libs.centipair.components.calendar
  (:require [libs.centipair.style :as style]
            [libs.centipair.components.input :as input]))


(def months [{:label "January" :value 1 :days 31}
             {:label "February" :value 2 :days 28}
             {:label "March" :value 3 :days 31}
             {:label "April" :value 4 :days 30}
             {:label "May" :value 5 :days 31}
             {:label "June" :value 6 :days 30}
             {:label "July" :value 7 :days 31}
             {:label "August" :value 8 :days 31}
             {:label "September" :value 9 :days 30}
             {:label "October" :value 10 :days 31}
             {:label "November" :value 11 :days 30}
             {:label "December" :value 12 :days 31}])


(defn day-calculator
  [month &[year]]
  (if (nil? year)
    (:days (nth months (dec month)))
    (if (and (= 0 (mod year 4)) (= month 2))
      29 ;;February leap year
      (:days (nth months (dec month))))))


(defn day-generator
  [month &[year]]
  (range 1 (inc (day-calculator month year))))


(defn update-month
  [field value]
  (do
    (swap! field assoc :month-value value)
    (swap! field assoc :days (day-generator value (:year-value @field)))))


(defn update-day
  [field value]
  (swap! field assoc :day-value value))



(defn select-day-option
  [field day]
  [:option {:key (str (:id @field) "-day-option-" day)
            :value day} day])



(defn select-month-option
  [field month]
  [:option {:key (str (:id @field) "-month-option-" (:value month))
            :value (:value month)} (:label month)])


(defn select-month
  [field]
  [:div {:class (if (nil? (:class-name @field)) style/bootstrap-input-container-class (:class-name @field))
         :key (str "month-container-" (:id @field))}
   [:label {:for (:id @field) :class "col-sm-4 control-label"
            :key (str "label-" (:id @field))} (:month-label @field)]
   [:div {:class "col-sm-2" :key (str "divider-container-" (:id @field))}
    [:select {:key (:id @field)
              :class "form-control"
              :id (:id (str "month-" @field))
              :on-change #(update-month field (-> % .-target .-value) )
              :value (:month-value @field)
              :disabled (if (:disabled @field) "disabled" "")}
     (doall (map (partial select-month-option field) months))]]])


(defn select-day
  [field]
  [:div {:class (if (nil? (:class-name @field)) style/bootstrap-input-container-class (:class-name @field))
         :key (str "day-container-" (:id @field))}
   [:label {:for (:id @field) :class "col-sm-4 control-label"
            :key (str "label-" (:id @field))} (:day-label @field)]
   [:div {:class "col-sm-2" :key (str "divider-container-" (:id @field))}
    [:select {:key (:id @field)
              :class "form-control"
              :id (:id (str "day-" @field))
              :on-change #(update-day field (-> % .-target .-value) )
              :value (:day-value @field)
              :disabled (if (:disabled @field) "disabled" "")}
     (doall (map (partial select-day-option field) (if (nil? (:month-value @field))
                                                     (day-generator 1)
                                                       (:days @field))))]]])

