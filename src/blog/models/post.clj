(ns blog.models.post
	(:use [cheshire.core])
	(:require [clojure.core.cache :as cache]))

(def C (cache/ttl-cache-factory (* 1000 60 5) {}))

(defn directory [path]
	(clojure.java.io/file path))

(def path (str "/home/paul/projects/noir/blog/posts"))

(defn meta-files []
	(def cached-meta-files (get C :meta_files))
	(if (= cached-meta-files nil)
		(let [metas (for [f (file-seq (directory path)) :when (re-find #"^[\w|\d -]+.json$" (.getName f))]
      		f)]
			(cache-set :meta_files metas))
		cached-meta-files))

(defn get-content [f]
	(if (not (nil? f))
		(slurp (str path "/" f))))

(defn get-all []
	(for [f (meta-files) :when (not (.isDirectory f)) :when (.exists f)]
		(decode (slurp f) true)))

(defn cache-set [key value]
	(println (str "[CACHE] New value for " key " : " [value]))
	(def C (-> C (assoc key value)))
	value)

(defn get-one [permalink]
	(def cached (get C (keyword permalink)))
	(if (= cached nil)
		(for [f (meta-files) :when (not (.isDirectory f)) :when (= (str (second (re-matches #"^([\w|\d -]+).md$" permalink)) ".json") (.getName f))]
			(cache-set (keyword permalink) (decode (slurp f) true)))
		[cached]))
