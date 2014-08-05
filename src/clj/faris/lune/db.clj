(ns clj.faris.lune.db
  (:require [monger.core :as mg]))

(defonce db (atom {}))

(defn connect!
  [{:keys [host port db-name]}]
  (when (empty? @db)
    (let [conn (mg/connect {:host host :port port})
          current-db (mg/get-db conn db-name)]
      (reset! db {:connection conn
                  :db current-db}))))

(defn disconnect!
  []
  (when-not (empty? @db)
    (mg/disconnect (:connection @db))
    (reset! db {})))
