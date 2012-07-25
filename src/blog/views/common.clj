(ns blog.views.common
	(:use [noir.core :only [defpartial]]
		  [hiccup.page :only [include-css include-js html5]]
		  [hiccup.element :only [javascript-tag link-to]]))

(defpartial typekit [kit]
	(include-js (str "http://use.typekit.com/" kit ".js"))
	[:script { :type "text/javascript" } "try{TypeKit.load();}catch(e){}" ])

(def includes { :base (include-css "/css/base.css")
				:skeleton (include-css "/css/skeleton.css")
				:layout (include-css "/css/layout.css")
				:typekit (typekit "bxp3bro")
				:jquery (include-js "http://code.jquery.com/jquery-1.7.2.min.js") })

(defpartial build-header [incls]
	[:head
		[:title "wwww.endou.fr"]
		
		[:meta {:charset "utf-8"}]
		[:meta {:name "description" :content "A crazy programmer's blog !"}]
		[:meta {:name "author" :content "Paul d'HUBERT"}]
		
		;For mobile web browsers 
		[:meta {:name "viewport" :content "width=device-width, initial-scale=1, maximum-scale=1"}]

		(map #(get includes %) incls)])

(defpartial header []
	[:div {:class "header"}
		(link-to "/" 
			[:embed {:src "logo.svg" :type "image/svg+xml"}])])

(defpartial layout [& content]
	(html5
		(build-header [:base :skeleton :layout :jquery :typekit])
		[:body
			(header)
			content]))

