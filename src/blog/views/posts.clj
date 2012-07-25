(ns blog.views.posts
  (:require [blog.views.common :as common]
  			[blog.models.post :as post])
  (:use [noir.core]
  		[noir.validation :as vali]
  		[hiccup.form]
  		[hiccup.element :only [link-to]]))

(defpartial post-list-item [{:keys [title content date author excerpt] :as post}]
	(when post
		[:div.post 
			[:h3 title]
			[:p.date date]
			[:p.content {:data-content excerpt} " "]
			
			(link-to {:class "readmore"} (str "post/" content) "Read More...")]))

(defpartial post-list-page [posts]
	(common/layout
		(map post-list-item posts)))

(defpartial post-item [{:keys [title content date author excerpt] :as post}]
	(when post
		[:div.post
			[:h3 title]
			[:p.date date]
			[:p.content {:data-content (post/get-content content)}]]))

(defpartial post-page [permalink]
	(common/layout)
		(map post-item (post/get-one permalink)))

(defpage [:get ["/post/:permalink" :permalink #"[\w|\d -]+.md$"]] {:keys [permalink]}
	(post-page permalink))

(defpage "/" []
	(post-list-page (post/get-all)))
