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

(defn- keyed-operator
  [operator]
  (condp = operator
    "in" :$in
    "or" :$or
    nil))

(defn- process-value-using-operator
  [value operator]
  (let [[head & tails] (reverse operator)
        add-operator (fn [parsed-operator value-for-operator]
                       (if (nil? parsed-operator)
                         value-for-operator
                         {parsed-operator value-for-operator}))]
    (if (nil? head)
      value
      (let [first-operator (keyed-operator head)
            first-value (add-operator first-operator value)]
        (if (nil? tails)
          first-value
          (reduce (fn [prev-value next-value]
                    (let [current-operator (keyed-operator next-value)
                          current-value (add-operator current-operator prev-value)]
                      current-value)) first-value tails))))))

(def mongo-query-grammar-transformer
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
                       (assoc prev-val next-key next-val)))
                   {} (mapcat identity value)))})

(defn parser
  [grammar]
  (if-not (= (type grammar) instaparse.core.Parser)
    (let [message "Grammar should be a type of instaparse.core.Parser."
          current-grammar-type (str (if (nil? grammar) "nil" (type grammar)))]
      {:message message
       :grammar-type current-grammar-type})
    (fn [query-string]
      (->> query-string
           grammar
           (insta/transform mongo-query-grammar-transformer)))))
