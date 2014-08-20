(ns clj.faris.lune.validator.db
  (:use juggler.core
        juggler.rule))

(defvalidator query-validator
  {:strict? true}
  :name [[required] [string]]
  :description [[required] [string]]
  :content [[required] [string]]
  :date-created [[required]]
  :deleted [[required]])

(defvalidator collection-validator
  {:strict? true}
  :name [[required] [string]]
  :description [[required] [string]]
  :query [[required] [is-vec] [every map?] [every query-validator]]
  :date-created [[required]]
  :deleted [[required]])

(defvalidator db-insert-validator
  {:strict? true}
  :_id [[required]]
  :name [[required] [string]]
  :collection [[required] [is-vec] [every map?] [every collection-validator]]
  :date-created [[required]]
  :deleted [[required]])
