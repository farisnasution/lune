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
    key = (word | number) (<dot> (word | number))*
    value = (word | number) (<cln> (word | number))*
    pair = key <eq> value <amp>*"))

(def transform-mongo-query-grammar
  {:alphabet #(identity %)
   :number #(read-string %)
   :word (fn [& value]
           (join "" value))
   :key (fn [& value]
          (->> value
               (join ".")
               keyword))
   :value (fn [& value]
            (apply conj [] value))
   :pair #(hash-map %1 %2)
   :expr (fn [& value]
           (apply conj {} value))})

(defn p
  [query-string]
  (->> query-string
       mongo-query-grammar
       (insta/transform transform-mongo-query-grammar)))