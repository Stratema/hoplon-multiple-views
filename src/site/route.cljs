(ns site.route
  (:require
   [cljs.core.match :refer-macros [match]]
   [javelin.core :refer-macros [defc= dosync]]
   [secretary.core :as sec]
   [site.app :as a]
   [site.views.home :as home]
   [site.views.about :as about]
   [site.views.account :as account]))

(def routes
  ["/"
   "/:resource"
   "/:resource/:action"
   "/:resource/:action/:stage"])

(defn add-routes [routes]
  (doseq [route routes]
    (sec/add-route!
     route (fn [{:keys [resource action stage] :as context}]
             (dosync
              (reset! a/context context)
              (reset! a/resource (keyword resource))
              (reset! a/action (keyword action))
              (reset! a/stage (keyword stage)))))))

(defc= view
  (match a/resource
         :home home/view
         :about about/view
         :account account/view
         :else home/view))
