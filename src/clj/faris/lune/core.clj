(ns clj.faris.lune.core
  (:use [clj.faris.lune.server :exclude [server]]
        [clj.faris.lune.db :exclude [db]]
        clj.faris.lune.settings
        clj.faris.lune.route.core))

(def default-profile :dev)

(defn boot!
  [& [prf]]
  (let [keyworded-prf (if (nil? prf) default-profile (keyword prf))
        {:keys [server db]} (keyworded-prf app-settings)]
    (start! app server)
    (connect! db)))

(defn shutdown!
  [& [prf]]
  (let [keyworded-prf (if (nil? prf) default-profile (keyword prf))
        {:keys [server]} (keyworded-prf app-settings)]
    (stop! (:port server))
    (disconnect!)))
