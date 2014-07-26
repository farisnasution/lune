(ns clj.faris.lune.middleware.core
  (:use [clojure.walk :only [keywordize-keys]]
        [ring.middleware [json :only [wrap-json-body]]]
        [compojure.handler :only [api]]
        [clj.faris.lune.util :only [header-value-to-map]]))

(defn keywordize-request-key
  [handler]
  (fn [request]
    (handler (keywordize-keys request))))

(def parse-json-body wrap-json-body)

(def create-api-suitable api)

(defn header-string-to-map
  [handler]
  (fn [request]
    (let [headers (:headers request)
          header-modifier (fn [f some-map]
                            (into {} (for [[k v] some-map] [k (cond
                                                               (string? v) (f v)
                                                               (map? v) (header-modifier f v)
                                                               :else v)])))
          modified-headers (header-modifier header-value-to-map headers)]
      (handler (assoc request :headers modified-headers)))))
