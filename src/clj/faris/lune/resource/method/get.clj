(ns clj.faris.lune.resource.method.get
  (:use [monger [collection :only [find-map-by-id]]]))

(defn get-entity-handler
  [collection-name id factory]
  {:handle-not-found []
   :exists? (fn [ctx]
              (let [entity (find-map-by-id collection-name id)]
                (when-not (nil? entity)
                  {:entity entity})))
   :handle-ok #(factory (:entity %))})
