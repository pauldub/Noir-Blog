(ns blog.server
  (:require [noir.server :as server]) 
  (:gen-class))

(server/load-views-ns 'blog.views)

(defn -main [& m]
  (let [mode (keyword (or (first m) :dev))
        port (Integer. (get (System/getenv) "PORT" "3001"))]
    (server/start port {:mode mode
                        :ns 'blog})))
