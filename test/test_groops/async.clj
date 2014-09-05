(ns test-groops.async
  (:require [groops.async :as async]
            [groops.server :as server]
            [http.async.client :as http]
            [clojure.data.json :as json]
            [test-groops.api :as api-test :only post-test-message])
  (:use clojure.test
        ring.mock.request)
 )

(comment
  (ns test-groops.async)
  (require '[groops.async :as async])
  (require '[http.async.client :as http])
  (require '[clojure.data.json :as json])
  (require '[test-groops.api :as api-test :only post-test-message])
  (use 'clojure.test)
  (use 'ring.mock.request))

(server/start-webserver)

(def client (http/create-client))

(def received-msg (atom nil))

(def ws (http/websocket client "ws://localhost:8080/chat-ws"
                        :text (fn [con msg]
                                (reset! received-msg msg)
                                (println "test-groops.async: ws text: connection " con)
                                (println "test-groops.async: ws text: message " msg))
                        :close (fn [con status]
                                 (println "test-groops.async: ws close:" con status))
                        :open (fn [con]
                                (println "test-groops.async: ws opened:" con))))

(http/send ws :text (pr-str {:name "Rich Hickey" :email "rich@clojure.com" :room "Beta"}))

(deftest websocket-populates-chat-client
  (let [chat-client (deref async/chat-clients)
        ws-msg (first (vals chat-client))]
    (is (> (count chat-client) 0))
    (is (= "Rich Hickey" (:name ws-msg)))
    (is (= "rich@clojure.com" (:email ws-msg)))
    (is (= "Beta" (:room ws-msg)))))

(api-test/post-test-message {:room "Beta" :user "Rich Hickey" :message "You're doing it wrong." :gravatar-url nil})

(deftest websocket-sends-to-client
  (let [msg-rec (first (vals (json/read-str @received-msg)))]
    (println "websocket-sends-to-client msg:" )
    (is (= "Rich Hickey" (get-in  msg-rec ["author"])))
    (is (= "You're doing it wrong." (get-in msg-rec ["message"])))))
