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
   (hoplon)
   (cljs :optimizations :advanced)
   (target :dir #{"target"})))
