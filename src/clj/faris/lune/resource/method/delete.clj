(ns clj.faris.lune.resource.method.delete
  (:use [monger [collection :only [update-by-id
                                   remove-by-id]]]))

(defn delete-entity-handler
  [collection-name id]
  {:delete! (fn [ctx]
              (let [entity (:entity ctx)
                    deleted (:deleted entity)]
                (if (or (false? deleted) (nil? deleted))
                  (update-by-id collection-name id (assoc entity :deleted true))
                  (remove-by-id collection-name id))))})
