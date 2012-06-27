(ns grabber.core
  (:require [http.async.client :as http])
  (:require [http.async.client.request :as request]))


(defn closing [client func]
  (fn [& args] (do
    (http/close client)
    (apply func args)
  )))

(defn async-get [url on-compete on-error]
  (let [client (http/create-client :follow-redirects true)
        request (request/prepare-request :get url)]
    (request/execute-request client request
                             :completed (closing client on-compete)
                             :error (closing client on-error))))

(defn async-multi [url times]
  (let [client (http/create-client :follow-redirects true)
        done 0]
    (dotimes [n times]
      (let [request (request/prepare-request :get url)]
        (request/execute-request client request
                                 :completed #(do % (println (str "done " n)))
                                 :error #(println %2))))))

; This is for testing purposes only
(defn sync-get [url on-compete on-error]
  (let [client (http/create-client :follow-redirects true)
        request (request/prepare-request :get url)
        result (request/execute-request client request
                             :completed (closing client on-compete)
                             :error (closing client on-error))]
        (http/await result)
        result
    ))

; This is for testing purposes only
(defn sync-get2 [url on-compete on-error]
  (with-open [client (http/create-client :follow-redirects true)]
    (let [request (request/prepare-request :get url)
          result (request/execute-request client request)]
          (http/await result)
          result)))

(defn sync-get3 [url on-compete on-error]
  (with-open [client (http/create-client :follow-redirects true)]
    (let [result (http/GET client url)]
      (http/await result)
      nil)))

(defn touch-client []
  (let [client (http/create-client)]
    (http/close client)))
