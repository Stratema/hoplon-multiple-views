(set-env!
 :dependencies '[[adzerk/boot-cljs          "1.7.228-1"]
                 [cljsjs/boot-cljsjs        "0.5.1"]
                 [adzerk/boot-reload        "0.4.8"]
                 [hoplon/boot-hoplon        "0.1.13"]
                 [hoplon/hoplon             "6.0.0-alpha16"]
                 [org.clojure/clojure       "1.8.0"]
                 [org.clojure/clojurescript "1.9.36"]
                 [tailrecursion/boot-jetty  "0.1.3"]

                 [org.clojure/core.match    "0.3.0-alpha4"]
                 [com.andrewmcveigh/cljs-time "0.3.14"]
                 [kamos/semantic-ui         "2.1.4-SNAPSHOT"] ;[cljsjs/semantic-ui        "2.1.8-0"]
                 [secretary                 "1.2.3"]]

 :source-paths   #{"src"}
 :resource-paths #{"assets"})

(require
 '[adzerk.boot-cljs         :refer [cljs]]
 '[adzerk.boot-reload       :refer [reload]]
 '[hoplon.boot-hoplon       :refer [hoplon prerender]]
 '[cljsjs.boot-cljsjs       :refer [from-cljsjs]]
 '[tailrecursion.boot-jetty :refer [serve]])

(deftask dev
  "Build site for local development."
  []
  (comp
   (from-cljsjs :profile :development)
   (sift :to-resource #{#"themes"})
   (sift :to-resource #{#"semantic-ui.inc.css"})
   (sift :move {#"^semantic-ui.inc.css$" "semantic-ui.css"})
   (watch)
   (speak)
   (hoplon :pretty-print true)
   (reload)
   (cljs)
   (serve :port 8000)
   (target :dir #{"target"})))

(deftask prod
  "Build site for production deployment."
  []
  (comp
   (from-cljsjs :profile :development)
   (sift :to-resource #{#"themes" #"semantic-ui.inc.css"})
   (sift :move {#"themes" "css/themes/" #"^semantic-ui.inc.css$" "css/semantic-ui.css"})
   (hoplon)
   (cljs :optimizations :advanced)
   (target :dir #{"target"})))

;; Might need this:
;; (from-cljsjs :profile :development)
;; (sift :to-resource #{#"themes" #"semantic-ui.inc.css"})
;; (sift :move {#"themes" "css/themes/" #"^semantic-ui.inc.css$" "css/semantic-ui.css"})
