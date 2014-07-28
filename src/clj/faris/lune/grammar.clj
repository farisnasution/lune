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

(defn keyed-operator
  [operator]
  (condp = operator
    "in" :$in
    "or" :$or
    :$random))

(defn process-value-using-operator
  [value operator]
  (let [[head & tails] (reverse operator)]
    (if (nil? head)
      value
      (let [first-operator (keyed-operator head)
            first-value {first-operator value}]
        (if (nil? tails)
          first-value
          (reduce (fn [prev-value next-value]
                    (let [current-operator (keyed-operator next-value)
                          current-value {current-operator prev-value}]
                      current-value)) first-value tails))))))

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
           (reduce (fn [prev-val [next-key next-val]]
                     (if (and (contains? prev-val next-key)
                              (map? next-val))
                       (let [current-value (next-key prev-val)
                             new-value (conj current-value next-val)]
                         (assoc prev-val next-key new-value))
                       (assoc prev-val next-key next-val))) {} (mapcat identity value)))})

(defn parser
  [query-string]
  (->> query-string
       mongo-query-grammar
       (insta/transform transform-mongo-query-grammar)))
