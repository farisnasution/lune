(ns clj.faris.lune.resource.core
  (:require [monger [query :as mq]]))

(defn media-types
  []
  {:available-media-types "application/json"})
