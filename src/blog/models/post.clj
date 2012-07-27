(ns blog.models.post
	(:use [cheshire.core])
	(:require [clojure.core.cache :as cache]))

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
			(into {} (reverse (sort-by :date (cache-set :meta_files metas)))))
		(into {} (reverse (sort-by :date cached-meta-files)))))

(defn get-content [filename]
	(let [cached-content (get C (keyword filename))]
		(if (= cached-content nil)
			(cache-set (keyword filename) (slurp (str path "/" filename)))
			cached-content)))

(defn get-all []
	(vals (meta-files)))

(defn get-one [permalink]
	((meta-files) (keyword permalink)))
