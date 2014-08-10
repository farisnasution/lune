(ns clj.faris.lune.resource.method.put
  (:use [monger [collection :only [update-by-id]]]))

(defn put-entity-handler
  [collection-name id factory]
  {:can-put-to-missing? false
   :put! (fn [ctx]
           (let [headers (-> ctx :request :headers)
                 body (:body ctx)
                 entity (:entity ctx)
                 value (factory (into entity body))]
             (update-by-id collection-name id value)))})
