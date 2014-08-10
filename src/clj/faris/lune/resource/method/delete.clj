(ns clj.faris.lune.resource.method.delete
  (:use [monger [collection :only [update-by-id
                                   remove-by-id]]]))

(defn delete-entity-handler
  [collection-name id factory]
  {:delete! (fn [ctx]
              (let [entity (-> ctx :entity factory)
                    deleted (:deleted entity)]
                (if (or (false? deleted) (nil? deleted))
                  (update-by-id collection-name id (assoc entity :deleted true))
                  (remove-by-id collection-name id))))})
