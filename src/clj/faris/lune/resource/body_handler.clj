(ns clj.faris.lune.resource.body-handler
  (:use [monger [collection :only [find-map-by-id]]]))

(defn authorization-checker
  [collection-name id]
  {:allowed? (fn [ctx]
               (let [entity (find-map-by-id collection-name id)
                     entity-user-id (-> entity :user :_id)
                     current-user-id (-> ctx :request :user :_id)]
                 [(and (not (or (nil? current-user-id) (nil? entity-user-id)))
                       (= current-user-id entity-user-id))
                  {:entity entity}]))})

(defn malformed-checker
  [validation-schema]
  {:malformed? (fn [ctx]
                 (let [body (-> ctx :request :body)
                       error (if (map? body)
                               (validation-schema body)
                               "Malformed type.")]
                   (if (empty? error)
                     [false {:body body}]
                     [true {:error error}])))
   :handle-malformed :error})
