(ns site.elements
  (:require
   [hoplon.core :as h
    :refer [a code div html-meta link]
    :refer-macros [defelem]]
   [javelin.core :refer-macros [cell=]]
   [site.app :as a]
   [site.nav :as n]
   [site.util :as u]
   [site.semantic :as s]))


(defelem menubar []
  (div :class "ui top fixed large inverted menu"
       (s/container
        (a :class "header item" :href "/home" "Home")
        (a :class "item" :href "/about" "About")
        (div :class (cell= {:right true :item true :hidden a/logged-in?})
             (a :class "ui inverted button" :href "/account" "Login")
             (a :class "ui inverted button" :href "/account/register" "Register"))
        (div :class (cell= {:right true :item true :hidden (not a/logged-in?)})
             (a :class "ui inverted button"
                :click #(swap! a/state dissoc :account) "Logout")))))

(defelem head [attrs kids]
  (h/head
   (html-meta :charset "utf-8")
   (html-meta :name "viewport"
              :content "width=device-width, initial-scale=1.0, maximum-scale=1.0")
   (link :href "//cdn.jsdelivr.net/semantic-ui/2.1.8/semantic.min.css" :rel "stylesheet" :media "screen")
   (link :href "/css/site.css" :rel "stylesheet" :media "screen")
   kids))
