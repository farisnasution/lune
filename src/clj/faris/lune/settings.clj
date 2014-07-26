(ns clj.faris.lune.settings)

(defn get-settings
  [path]
  (->> path
       slurp
       read-string
       (drop 2)
       (cons :version)
       (apply hash-map)))

(def settings (get-settings "project.clj"))

(def app-settings (:app-settings settings))
