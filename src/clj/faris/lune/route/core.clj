(ns clj.faris.lune.route.core
  (:use compojure.core
        [compojure.route :only [resources]]))

(defn app
  [request]
  {:body request
   :status 200
   :headers {"Content-Type" "text/html"}})
