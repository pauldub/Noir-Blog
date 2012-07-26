(defproject blog "0.1.0-SNAPSHOT"
            :description "FIXME: write this!"            
            :dependencies [[org.clojure/clojure "1.4.0"]
                           [noir "1.3.0-beta3"]
                           [org.clojure/core.cache "0.5.0"]
                           [cheshire "4.0.1"]]
			:dev-dependencies [[lein-daemon "0.4.2"]]
           	:daemon {
           		:blogd {
           			:ns blog.server
           			:pidfile "/var/run/blogd"}}
            :main blog.server)

