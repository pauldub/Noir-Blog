(ns blog.models.post
	(:use [cheshire.core :only [decode]])
	(:require [clojure.core.cache :as cache]
			  [clj-time.core :as time]
			  [clj-time.format :as time-format]))

(def cache (cache/ttl-cache-factory (* 1000 1) {}))

(defn directory [path]
	(clojure.java.io/file path))

(def meta-files-path (str "/home/paul/projects/noir/blog/posts"))

(defn cache-set [key value]
	(println (str "[CACHE] New value for " key "\n"))
	(def cache (-> cache (assoc key value)))
	value)

(defn meta-files []
	(def cached-meta-files (get cache :meta_files))
	(if (= cached-meta-files nil)
		(let [metas (for [file (file-seq (directory meta-files-path)) 
				:when (not (.isDirectory file)) 
				:when (.exists file) 
				:when (re-find #"^[\w|\d -]+.json$" (.getName file))]
      		(let [json (decode (slurp file) true)]
      			[(keyword (json :content)) json]))]
			(into {} (cache-set :meta_files metas)))
		(into {} cached-meta-files)))

(defn get-content [filename]
	(let [cached-content (get cache (keyword filename))]
		(if (= cached-content nil)
			(cache-set (keyword filename) (slurp (str meta-files-path "/" filename)))
			cached-content)))

(defn comp-cached-meta-files-date
	[el1 el2]
		(let [[filename1 metas1] el1 [filename2 metas2] el2]
			(time/after? 
				(time-format/parse (time-format/formatter "dd/MM/yy") (metas1 :date)) 
				(time-format/parse (time-format/formatter "dd/MM/yy") (metas2 :date)))))

(defn get-all []
	(vals (sort (comp comp-cached-meta-files-date) (meta-files))))

(defn get-one [permalink]
	((meta-files) (keyword permalink)))