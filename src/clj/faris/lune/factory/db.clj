(ns clj.faris.lune.factory.db
  (:use [clj.faris.lune.util :only [defmapper]])
  (:import org.bson.types.ObjectId))

(defmapper db-jsonable
  :_id str)

(defmapper db-saveable
  :_id #(ObjectId. %))
