(ns blog.views.posts
  (:require [blog.views.common :as common]
  			[blog.models.post :as post])
  (:use [noir.core]
        [noir.response :only [jsonp json content-type]]
  		[hiccup.element :only [javascript-tag link-to]]))

(defpartial disqus-thread []
  [:div#disqus_thread]
  (javascript-tag "var disqus_shortname = 'endou';(function() { var dsq = document.createElement('script'); dsq.type = 'text/javascript'; dsq.async = true; dsq.src = 'http://' + disqus_shortname + '.disqus.com/embed.js'; (document.getElementsByTagName('head')[0] || document.getElementsByTagName('body')[0]).appendChild(dsq); })();"))

(defpartial post-list-item [{:keys [title content date author excerpt] :as post}]
	(when post
		[:div.post 
			[:h3 (link-to (url-for post {:permalink content }) title)]
			[:p.date date]
			[:p.content excerpt]
			[:br]
			(link-to {:class "readmore"} (url-for post {:permalink content}) "Read More...")]))

(defpartial post-list-page [posts]
	(common/layout "www.endou.fr"
		(map post-list-item posts)))

(defpartial post-item [{:keys [title content date author excerpt] :as post}]
	(when post
		[:div.post
			[:h3 title]
			[:p.date date]
			[:p.content (post/get-content content)]
            [:div.g-plusone {:href (url-for post {:permalink content})
                             :data-size "medium"
                             :data-count "false" }]
            [:a.twitter-share-button { :href "https://twitter.com/share" 
                                       :data-via "ElMoustache" 
                                       :data-count "none" }
                "Tweet"]
            (javascript-tag "!function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0];if(!d.getElementById(id)){js=d.createElement(s);js.id=id;js.src=\"http://platform.twitter.com/widgets.js\";fjs.parentNode.insertBefore(js,fjs);}}(document,\"script\",\"twitter-wjs\");")
            [:br]
            [:div.g-plus-wrapper 
            [:div.g-plus { :data-height "69"  
                           :data-href "http://plus.google.com/102963729418932964613" 
                           :data-rel "author" }]
            [:a.geeklist { :href "javascript:document.getElementsByTagName('body')[0].appendChild(document.createElement('script')).setAttribute('src','http://geekli.st/javascript/bookmarklet/bookmarklet.js')" }
              [:span.mirror "g"]
              [:span "g"]]]]))

(defpartial post-page [permalink]
  (let [post (post/get-one permalink)]
	(common/layout (str (post :title) " - www.endou.fr")
		(post-item post)
        (disqus-thread))))

(defpage post [:get ["/post/:permalink" :permalink #"[\w|\d -]+.md$"]] {:keys [permalink]}
	(post-page permalink))

(defpage "/posts.json" {:keys [callback]}
    (content-type "application/json" (jsonp callback (post/meta-files-with-id))))

(defpage "/" []
	(post-list-page (post/get-all)))
