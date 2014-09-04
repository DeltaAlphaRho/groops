(ns test-groops.async
  (:require [groops.async :as async]
            [http.async.client :as http])
  (:use clojure.test
        ring.mock.request)
 )

(comment
  (ns test-groops.async)
  (require '[groops.async :as async])
  (require '[http.async.client :as http])
  (use 'clojure.test)
  (use 'ring.mock.request))

(comment
  (def basic-req (request :get "/"))

  (def client (http/create-client))

  (def latch (promise))

  (def received-msg (atom nil))
  (def connection (atom nil))

  (def ws (http/websocket client "ws://localhost:8080/chat-ws"
                          :text (fn [con msg]
                                  (println "ws text:" con msg))
                          :close (fn [con status]
                                   (println "ws close:" con status))
                          :open (fn [con]
                                  (println "ws opened:" con))))

  (http/send ws :text (pr-str {:name "Rich Hickey" :email "rich@clojure.com" :room "Alpha"}))


;;;(def response (http:/GET client "http://localhost:8080/chat-ws"))
  )
