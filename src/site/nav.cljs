(ns site.nav
  (:import
   [goog History]
   [goog.history Html5History]
   [goog Uri])
  (:require
   [goog.events :as events]
   [goog.history.EventType :as EventType]
   [secretary.core :refer [dispatch! locate-route]]
   [site.app :as a]))

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
                 (.setUseFragment false)
                 (.setPathPrefix ""))
               (History.)))

(defn parse-uri
  "Expand the uri into the component parts and return as a map"
  [uri]
  (let [u (.parse Uri uri)]
    {:scheme (.getScheme u)
     :domain (.getDomain u)
     :port (.getPort u)
     :path (.getPath u)
     :query (.getQuery u)
     :fragment (.getFragment u)}))

(defn pqf
  "Concatenate the path, query and fragment from an expanded uri"
  [u]
  (str (:path u)
       (when-let [q (not-empty (:query u))] (str \? q))
       (when-let [f (not-empty (:fragment u))] (str \# f))))

(defn on-document-click [e]
  ;; If target is A then great
  ;; If target is I, an icon, then get parent A and substitute for the target
  (let [tag-name (.. e -target -tagName)
        parent (.. e -target -parentElement)
        target (cond (= tag-name "A")
                     (.-target e)
                     (= tag-name "I")
                     (if (= (.-tagName parent) "A") parent false)
                     :else false)]
    (when target
      (let [href (.-href target)
            title (.-title target)
            {:keys [path fragment] :as u} (parse-uri href)
            route (locate-route path)]
        (when route
          (.setToken history (pqf u) title)
          (.preventDefault e))))))

(defn on-history-navigate [e]
  (dispatch! (.-token e)))

(defn setup-navigation []
  (.setEnabled history true)
  (events/listen js/document "click" on-document-click)
  (events/listen history EventType/NAVIGATE on-history-navigate))

(defn dispatch-path! []
  (let [{:keys [path query] :as uri} (parse-uri (.-location js/window))]
    (when (and (not= path "/")
               (not= path "/home"))
      (dispatch! (pqf uri)))))
