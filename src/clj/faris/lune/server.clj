(ns clj.faris.lune.server
  (:use org.httpkit.server))

(defonce server (atom {}))

(defn start!
  [handler opts]
  (let [keyworded-port (-> opts :port str keyword)]
    (when (nil? (keyworded-port @server))
      (swap! server assoc keyworded-port (run-server handler opts)))))

(defn stop!
  [port]
  (let [keyworded-port (-> port str keyword)
        server-instance (keyworded-port @server)]
    (when-not (nil? server-instance)
      (server-instance :timeout 100)
      (swap! server dissoc keyworded-port))))
