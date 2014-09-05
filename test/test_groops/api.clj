(ns test-groops.api
  (:require [groops.api :as api]
            [groops.data :as data]
            [clojure.data.json :as json])
  (:use clojure.test
        ring.mock.request))

(comment
  (ns test-groops.api)
  (require '[groops.api :as api])
  (require '[groops.data :as data])
  (require '[clojure.data.json :as json])
  (use 'clojure.test)
  (use 'ring.mock.request))

#_(defn request [method params]
  (hash-map :request-method method :headers {} :params params))

(defn user-map [name email twitter]
  (hash-map :name name :email email :twitter twitter))

(def test-users #{(user-map "John McCarthy" "john@lisp.com" "@JohnMcCarthy")
                  (user-map "Steve Russell" "steverussell@ibm.com" "@SteveRussell")
                  (user-map "Guy Steele" "guysteele@mit.edu" "@GuySteele")
                  (user-map "Rich Hickey" "rich@clojure.com" " @RickHickey")})

(defn post-test-user [user]
  (api/api-routes (-> (request :post "/api/user")
                      (assoc :params user))))

(doall (map post-test-user test-users))

(deftest test-post-user-count
  (is (= 4 (count (deref data/registry-set)))))

(comment
  (api/post-room (request :post {:room-name "Alpha"}))
  (api/post-room (request :post {:room-name "Beta"}))
  (api/post-room (request :post {:room-name "Gamma"}))
  (api/post-room (request :post {:room-name "Delta"})))

(defn post-test-room [room]
  (api/api-routes (-> (request :post "/api/room")
                      (assoc :params {:room-name room}))))

(def test-rooms #{"Alpha" "Beta" "Gamma" "Delta"})

(doall (map post-test-room test-rooms))

(deftest test-post-room-count
  (is (= 4 (count (deref data/room-set)))))


(defn get-user-in-room-count []
  (let [room-count-map (json/read-str 
                        (:body 
                         (api/api-routes (request :get "/api/rooms"))))]
    (apply + (vals (first (vals room-count-map))))))

(deftest test-get-rooms-initially-zero
  (is (= 0 (get-user-in-room-count))))

(defn get-message-vect-from-room [room]
  (first 
   (vals 
    (json/read-str 
     (:body (api/api-routes 
             (request :get (str "/api/room/messages/" room))))))))

(defn message-map [room user message gravatar-url]
  (hash-map :room room :user user :message message 
            :gravatar-url gravatar-url))

(def test-messages #{(message-map "Alpha" "John McCarthy"
                                  "This is the first room"
                                  nil)
                     (message-map "Alpha" "Steve Russell"
                                  "This is the second message in the first room"
                                  nil)})

(defn post-test-message [message]
  (api/api-routes (-> (request :post "/api/room/message")
                      (assoc :params message))))

(doall (map post-test-message test-messages))

(deftest count-message-vect
  (is (= 2 (count (get-message-vect-from-room "Alpha"))))
  ;; messages loaded into Beta froom in async testing
  (is (= 1 (count (get-message-vect-from-room "Beta"))))
  (is (empty? (get-message-vect-from-room "Gamma")))
  (is (empty? (get-message-vect-from-room "Delta"))))


