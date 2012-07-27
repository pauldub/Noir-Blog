(ns blog.models.post
	(:use [cheshire.core :only [decode]])
	(:require [clojure.core.cache :as cache]
			  [clj-time.core :as time]
			  [clj-time.format :as time-format]))

(def C (cache/ttl-cache-factory (* 1000 60 5) {}))

(defn directory [path]
	(clojure.java.io/file path))

(def path (str "/home/paul/projects/noir/blog/posts"))

(defn cache-set [key value]
	(println (str "[CACHE] New value for " key " : " [value]))
	(def C (-> C (assoc key value)))
	value)

(defn meta-files []
	(def cached-meta-files (get C :meta_files))
	(if (= cached-meta-files nil)
		(let [metas (for [file (file-seq (directory path)) :when (not (.isDirectory file)) :when (.exists file) :when (re-find #"^[\w|\d -]+.json$" (.getName file))]
      		(let [json (decode (slurp file) true)]
      			[(keyword (json :content)) json]))]
			(into {} (cache-set :meta_files metas)))
		(into {} cached-meta-files)))

(defn get-content [filename]
	(let [cached-content (get C (keyword filename))]
		(if (= cached-content nil)
			(cache-set (keyword filename) (slurp (str path "/" filename)))
			cached-content)))

(defn comp-cached-meta-files
	[el1 el2]
		(let [[filename1 metas1] el1 [filename2 metas2] el2]
			(time/after? 
				(time-format/parse (time-format/formatter "dd/MM/yy") (metas1 :date)) 
				(time-format/parse (time-format/formatter "dd/MM/yy") (metas2 :date)))))

(defn get-all []
	(vals (sort (comp comp-cached-meta-files) (meta-files))))

(defn get-one [permalink]
	((meta-files) (keyword permalink)))