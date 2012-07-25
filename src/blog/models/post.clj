(ns blog.models.post
	(:use [cheshire.core]))

(defn directory [path]
	(clojure.java.io/file path))

(def path (str "/home/paul/projects/noir/blog/posts"))

(def meta-files 
	(for [f (file-seq (directory path)) :when (re-find #"^[\w|\d -]+.json$" (.getName f))]
      f))

(defn get-content [f]
	(if (not (nil? f))
		(slurp (str path "/" f))))

(defn debug-meta [f]
	(println f)
	(println (slurp f))
	(decode (slurp f) true))

(defn get-all []
	(for [f meta-files :when (not (.isDirectory f))]
		(decode (slurp f) true)))

(defn get-one [permalink]
	(for [f meta-files :when (not (.isDirectory f)) :when (= (str (second (re-matches #"^([\w|\d -]+).md$" permalink)) ".json") (.getName f))]
		(decode (slurp f) true)))