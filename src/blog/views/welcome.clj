(ns blog.views.welcome
  (:require [blog.views.common :as common])
  (:use [noir.core :only [defpage]]))

(defpage "/" []
	(common/layout
		[:h3 "Endou"]
		[:p "Hello world !"]
		[:a {:href "#" :style "float: right;"}
			"Read more..."]))

; (defpage "/welcome" []
;          (common/layout
;            [:p "Welcome to blog"]))