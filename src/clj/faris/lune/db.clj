(ns clj.faris.lune.db
  (:require [monger.core :as mg]))

(defonce db (atom nil))

(defn connect!
  [{:keys [host port db-name]}]
  (when (nil? @db)
    (let [_ (mg/connect! {:host host :port port})
          current-db (mg/get-db db-name)]
      (reset! db current-db)
      (mg/set-db! current-db))))

(defn disconnect!
  []
  (when-not (nil? @db)
    (mg/disconnect!)
    (reset! db nil)))
