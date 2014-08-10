(ns clj.faris.lune.resource.method.put
  (:use [monger [collection :only [update-by-id]]]))

(defn put-entity-handler
  [collection-name id factory]
  {:can-put-to-missing? false
   :put! (fn [ctx]
           (let [headers (-> ctx :request :headers)
                 value (factory (:body ctx) headers)]
             (update-by-id collection-name id value)))})
