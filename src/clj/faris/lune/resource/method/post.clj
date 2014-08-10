(ns clj.faris.lune.resource.method.post
  (:use [monger [collection :only [insert-and-return]]]))

(defn post-entity-handler
  [collection-name pre-factory post-factory]
  {:post! (fn [ctx]
            (let [body (:body ctx)
                  headers (-> ctx :request :headers)
                  result (insert-and-return collection-name (pre-factory body))]
              {:result (post-factory result)}))
   :handle-created :result})
