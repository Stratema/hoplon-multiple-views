(ns site.nav
  (:require
   [goog.events :as events]
   [goog.history.EventType :as EventType]
   [secretary.core :refer [dispatch! locate-route]]
   [site.app :as a]
   [site.util :as u]))

(defn push
  ([path] (push path nil))
  ([path title] (.setToken a/history path title)))

(defn scroll-to [fragment]
  (let [pos (if-let [offset (and (not-empty fragment)
                                 (-> (js/$ (str "#" fragment))
                                     (.first)
                                     (.offset)))]
              (- (.-top offset) 50)
              0)]
    (.log js/console "Scrolling to:" pos)
    (.animate (js/$ "html,body") #js {:scrollTop pos} 750)))

(defn on-document-click [e]
  ;; If target is A then great
  ;; If target is I, an icon, then get parent A and substitute for the target
  ;; (.log js/console "Click target:" (.-target e))
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
            {:keys [path fragment] :as u} (u/parse-uri href)
            route (locate-route path)]
        (.log js/console "uri:" (clj->js u))
        (.log js/console "route:" (clj->js route))
        (when route
          (push (u/pqf u) title)
          (.preventDefault e)
          (scroll-to fragment))))))

(defn on-history-navigate [e]
  (.log js/console "Dispatch on path:" (.-token e))
  (dispatch! (.-token e)))

(defn setup-navigation []
  (.setEnabled a/history true)
  (events/listen js/document "click" on-document-click)
  (events/listen a/history EventType/NAVIGATE on-history-navigate))

(defn dispatch-path! []
  (let [{:keys [path query] :as uri} (u/parse-uri (.-location js/window))]
    (.log js/console path)
    (if (and (not= path "/")
             (not= path "/home"))
      (dispatch! (u/pqf uri)))))
