(ns clj.faris.lune.grammar
  (:require [instaparse.core :as insta])
  (:use [clojure.string :only [split join]]))

(def mongo-query-grammar
  (insta/parser
   "expr = pair+
    <eq> = '='
    <amp> = '&'
    <cln> = ':'
    <dot> = '.'
    number = #'[0-9]+'
    alphabet = #'[a-zA-Z]+'
    word = number* alphabet (number* alphabet*)*
    key = (word | number) (<dot> (word | number))* operator*
    value = (word | number) (<cln> (word | number))*
    pair = key <eq> value <amp>*
    operator = #'__[a-zA-Z]+'"))

(defn process-value-using-operator
  [value operator]
  (identity value))

(def transform-mongo-query-grammar
  {:alphabet #(identity %)
   :number #(read-string %)
   :word (fn [& value]
           (join "" value))
   :operator #(identity %)
   :key (fn [& value]
          (reduce (fn [prev-val next-val]
                    (str prev-val (if (or (empty? prev-val)
                                          (.startsWith next-val "__"))
                                    "" ".") next-val)) "" value))
   :value (fn [& [head & tails :as value]]
            (if (nil? tails)
              head
              (apply conj [] value)))
   :pair (fn [key value]
           (let [[key & operator] (split key #"__")
                 key (keyword key)
                 value (process-value-using-operator value operator)]
             (hash-map key value)))
   :expr (fn [& value]
           (apply conj {} value))})

(defn parser
  [query-string]
  (->> query-string
       mongo-query-grammar
       (insta/transform transform-mongo-query-grammar)))
