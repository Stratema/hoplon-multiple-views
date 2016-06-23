(ns site.util
  (:import
   [goog userAgent]
   [goog Uri])
  (:require
   [javelin.core :refer [cell] :refer-macros [cell=]]
   [clojure.string :refer [join replace]]
   [clojure.walk :refer [postwalk]]
   [cljs.core.match :refer-macros [match]]
   [cljs-time.core :as t]
   [goog.userAgent :as ua]
   [goog.string :as gstring]
   [goog.string.format]))

(defn remove-empties
  [m]
  (let [f (fn [[k v]] (when (or (and (string? v) (not (empty? v)))
                                (and (not (string? v)) (not (nil? v)))) [k v]))]
    (postwalk (fn [x] (if (map? x) (into {} (map f x)) x)) m)))

(defn legacy-browser? []
  (and (.-IE userAgent) ; IE9 and below are considered broken
       (< (ua/compare (.-VERSION userAgent) "9.0") 1)))

(defn parse-int-or-str
  "If s contains only integer digits parse it as an integer, otherwise just return the string"
  [s]
  (if (re-matches #"[0-9]+" s)
    (js/parseInt s 10)
    s))

(defn path-cell [c path]
  (cell= (get-in c path) (partial swap! c assoc-in path)))

(defn modify-date [date comp value]
  (let [date' (.clone date)]
    (match comp
           :year (.setYear date' value)
           :month (.setMonth date' (dec value))
           :day (.setDate date' value))
    date'))

(defn date-cell [c comp]
  (cell= (match comp
                :year (t/year c)
                :month (t/month c)
                :day (t/day c))
         (partial swap! c modify-date comp)))

(defn combine
  "Merge maps, recursively merging nested maps whose keys collide."
  [m1 m2]
  (reduce (fn [m1 [k2 v2]]
            (if-let [v1 (get m1 k2)]
              (if (and (map? v1) (map? v2))
                (assoc m1 k2 (combine v1 v2))
                (assoc m1 k2 v2))
              (assoc m1 k2 v2)))
          m1 m2))

(defn format
  "Formats a string using goog.string.format."
  [fmt & args]
  (apply gstring/format fmt args))

(defn parse-uri "Expand the uri into the component parts and return as a map"
  [uri]
  (let [u (.parse Uri uri)]
    {:scheme (.getScheme u)
     :domain (.getDomain u)
     :port (.getPort u)
     :path (.getPath u)
     :query (.getQuery u)
     :fragment (.getFragment u)}))

(defn pqf
  "Return the path, query and fragment from an expanded uri"
  [u]
  (str (:path u)
       (when-let [q (not-empty (:query u))] (str \? q))
       (when-let [f (not-empty (:fragment u))] (str \# f))))

(defn today []
  (let [now (new js/Date)]
    (join "-" [(.getFullYear now)
               (gstring/padNumber (inc (.getMonth now)) 2)
               (gstring/padNumber (.getDate now) 2)])))

(defn add-errors-to-form [selector errors]
  (-> (js/$ selector)
      (.removeClass "success")
      (.addClass "error")
      (.form "add errors" (clj->js errors))))

(defn lookup [col key]
  (some #(if (= (:value %) key) (:name %)) col))
