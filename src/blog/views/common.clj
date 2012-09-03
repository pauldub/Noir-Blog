(ns blog.views.common
    (:require [blog.models.post :as post])
	(:use [noir.core :only [defpartial defpage]]
          [noir.response :only [content-type]]
		  [hiccup.page :only [include-css include-js html5]]
		  [hiccup.element :only [javascript-tag link-to]]
		  [hiccup.form]
          [clojure.xml]))

(defpartial typekit [kit]
	(include-js (str "http://use.typekit.com/" kit ".js"))
	[:script { :type "text/javascript" } "try{Typekit.load();}catch(e){}" ])

(def includes { :base (include-css "/css/base.css")
				:skeleton (include-css "/css/skeleton.css")
				:layout (include-css "/css/layout.css")
				:typekit (typekit "bxp3bro")
				:jquery (include-js "http://code.jquery.com/jquery-1.7.2.min.js")
				:showdown (include-js "https://raw.github.com/coreyti/showdown/master/src/showdown.js")
				:highlight_css (include-css "http://yandex.st/highlightjs/7.0/styles/zenburn.min.css")
				:highlight_js (include-js "/js/highlight.pack.js") })

(defpartial build-header [title description incls]
	[:head
		[:title title]
		
		[:meta {:charset "utf-8"}]
		[:meta {:name "description" :content description}]
		[:meta {:name "author" :content "Paul d'HUBERT"}]
		[:meta {:name "keywords" :content "Paul,d'Hubert,development,ux,ui,html,html 5,css,node.js,node,ruby,php,agile,nord,lille"}]
		;For mobile web browsers 
		[:meta {:name "viewport" :content "width=device-width, initial-scale=1, maximum-scale=1"}]
		(map #(get includes %) incls)
		(javascript-tag "var _gaq = _gaq || []; _gaq.push(['_setAccount', 'UA-33686813-1']); _gaq.push(['_setDomainName', 'endou.fr']); _gaq.push(['_trackPageview']); (function() {var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true; ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js'; var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s); })();")
        (javascript-tag "(function() { var po = document.createElement('script'); po.type = 'text/javascript'; po.async = true; po.src = 'https://apis.google.com/js/plusone.js'; var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(po, s); })();")
		])

(defpartial header []
	[:div {:class "header"}
		(link-to "/" 
			[:embed {:src "/logo.svg" :type "image/svg+xml"}])])

(defpartial layout [title description & content]
	(html5
		(build-header title description [:base :skeleton :layout :jquery :showdown :highlight_css :highlight_js :typekit])
		[:body
			(header)
			content
			(javascript-tag "hljs.initHighlightingOnLoad();")
			(javascript-tag "$(function(){ var p = $('.post .content'); $.each(p, function(index, p){ $(p).html(new Showdown.converter().makeHtml($(p).text())); }); });")]))

(defpage "/sitemap.xml" []
       (content-type "text/xml" (str "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">"
              "<url>\n"
              "<loc>" "http://www.endou.fr/" "</loc>\n"
              "<lastmod>" (.replaceAll (re-matcher #"([\d]+)/([\d]+)/([\d]+)" ((first (post/get-all)) :date)) "$3-$2-$1") "</lastmod>\n"
              "<changefreq>" "weekly" "</changefreq>\n"
              "<priority>" "1.0" "</priority>\n" 
              "</url>\n"
       (apply str (for [post (post/get-all)]
           (str "<url>\n"
                "<loc>" "http://www.endou.fr/" (post :content) "</loc>\n"
                "<lastmod>" (.replaceAll (re-matcher #"([\d]+)/([\d]+)/([\d]+)" (post :date)) "$3-$2-$1") "</lastmod>\n"
                "<changefreq>" "monthly" "</changefreq>\n"
                "<priority>" "0.8" "</priority>\n" 
                "</url>\n")))
            "</urlset>")))
