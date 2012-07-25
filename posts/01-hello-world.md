This is the traditional "Hello world" blog post, first of all this blog is written in Clojure using the Noir webframework
it uses static MarkDown and JSON files for the post contents and metadatas.

Now let's do some testing for markdown :

# Given I use
## MarkDown, I should
### Test headings and
#### (un)ordererd lists ?
 
 - This is
 - a quite short
 - list.

------

 1. Order is 
 2. cool
 3. but Chaos's
 4. better.

 ------

##### But the most important part is :

I want to post some crazy code on this blog !

###### LISP !
	
<pre>
	<code>
	(def meta-files 
		(for [f (file-seq (directory path)) :when (re-find #"^[\w|\d -]+.json$" (.getName f))]
      		f))
    </code>
 </pre>
