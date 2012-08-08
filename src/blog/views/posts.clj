(ns blog.views.posts
  (:require [blog.views.common :as common]
  			[blog.models.post :as post])
  (:use [noir.core]
  		[hiccup.element :only [javascript-tag link-to]]))

(defpartial disqus-thread []
  [:div#disqus_thread]
  (javascript-tag "var disqus_shortname = 'endou';(function() { var dsq = document.createElement('script'); dsq.type = 'text/javascript'; dsq.async = true; dsq.src = 'http://' + disqus_shortname + '.disqus.com/embed.js'; (document.getElementsByTagName('head')[0] || document.getElementsByTagName('body')[0]).appendChild(dsq); })();"))

(defpartial post-list-item [{:keys [title content date author excerpt] :as post}]
	(when post
		[:div.post 
			[:h3 (link-to (url-for post {:permalink content }) title)]
			[:p.date date]
			[:p.content {:data-content excerpt} " "]
			[:br]
			(link-to {:class "readmore"} (url-for post {:permalink content}) "Read More...")]))

(defpartial post-list-page [posts]
	(common/layout
		(map post-list-item posts)))

(defpartial post-item [{:keys [title content date author excerpt] :as post}]
	(when post
		[:div.post
			[:h3 title]
			[:p.date date]
			[:p.content {:data-content (post/get-content content)}]]
        (disqus-thread)))

(defpartial post-page [permalink]
	(common/layout)
		(post-item (post/get-one permalink)))

(defpage post [:get ["/post/:permalink" :permalink #"[\w|\d -]+.md$"]] {:keys [permalink]}
	(post-page permalink))

(defpage "/" []
	(post-list-page (post/get-all)))
