(ns site.elements
  (:require-macros
   [hoplon.core :refer [defelem]])
  (:require
   [hoplon.core :as h :refer [a code div html-meta link]]
   [site.app :as a]
   [site.nav :as n]
   [site.util :as u]
   [site.semantic :as s]))


(defelem menubar []
  (div :class "ui top inverted fixed menu"
       (s/container
        (a :class "header item" :href "/home" "Home")
        (a :class "item" :href "/about" "About"))))

(defelem head [attrs kids]
  (h/head
   (html-meta :charset "utf-8")
   (html-meta :name "viewport"
              :content "width=device-width, initial-scale=1.0, maximum-scale=1.0")
   (link :href "/semantic-ui.css" :rel "stylesheet" :media "screen")
   kids))
