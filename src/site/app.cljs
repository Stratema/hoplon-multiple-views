(ns site.app
  (:require-macros
   [javelin.core :refer [defc defc=]])
  (:import
   [goog History]
   [goog.history Html5History])
  (:require
   [hoplon.storage-atom :refer [local-storage]]
   [javelin.core :refer [cell]]))

;; Fix goog History to prevent it repeating the querystring
;; From http://www.lispcast.com/mastering-client-side-routing-with-secretary-and-goog-history
(aset js/goog.history.Html5History.prototype "getUrl_"
      (fn [token]
        (this-as this
                 (if (.-useFragment_ this)
                   (str "#" token)
                   (str (.-pathPrefix_ this) token)))))

(def history (if (.isSupported Html5History)
               (doto (Html5History.)
                 (.setUseFragment true)
                 (.setPathPrefix ""))
               (History.)))

(defc context nil)
(defc resource nil)
(defc action nil)
(defc stage nil)
(defc path nil)
(defc token nil)
