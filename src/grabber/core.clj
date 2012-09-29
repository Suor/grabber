(ns grabber.core
  ; (:use [lamina.core])
  ; (:use [aleph.http])
  (:use [net.cgrand.enlive-html :only (html-resource)])
  )


; (defn async-get [url]
;   (async
;     (loop [response (http-request {:method :get, :url url})]
;       (if (contains? #{301 302 307} (:status response))
;         (recur (http-request {:method :get, :url (get-in response [:headers "location"])}))
;         response))))

(def page (slurp "data/blog.html"))

(defn html-file [filename]
  (-> filename slurp java.io.StringReader. html-resource))

; (defn walk [html]
;   (tree-seq :content :content (first html)))

; (defn indexed [coll]
;   (map-indexed vector coll))

; (defn make-assoc [node]
;   (if (associative? node) node {:value node}))

; (defn add-index [index node]
;   (assoc (make-assoc node) :index index))

; (defn children [node]
;   (map-indexed add-index (:content node)))

; (defn children [node]
;   (map #(assoc (make-assoc %) :parent node) (:content node)))

(defn children [node]
  (map-indexed (fn [i el] [el {:parent node :index i}]) (:content node)))

(defn walk [html]
  (mapcat #(tree-seq (comp :content first) (comp children first) [% nil]) html))

(defn nodes [coll] (map first coll))
; (defn walk3 [html]
;   (lazy-cat (map #(tree-seq :content :content %) html)))

(defn node-value [node] (:tag node node))

(defn node-values [tree]
  (map (comp node-value first) tree))


(defn header? [node] (#{:h1 :h2 :h3 :h4} (:tag node)))

(defn formatted? [node] (#{:b :i :u :em :strong} (:tag node)))

(defn text-container? [node]
    (when-let [content (:content node)]
        (and (= 1 (count content)) (string? (first content)))))

(defn x-filter [filters tree]
    (filter (apply every-pred (map #(comp % first) filters)) tree))

; (filter (comp header? first) tree)
