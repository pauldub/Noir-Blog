(ns blog.views.welcome
  (:require [blog.views.common :as common]
            [noir.content.getting-started])
  (:use [noir.core :only [defpage]]))[hiccup.page :only [include-css html5]

(defpage "/welcome" []
         (common/layout
           [:p "Welcome to blog"]))