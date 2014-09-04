(ns test-groops.web
  (:require [groops.web :as web])
  (:use clojure.test
        ring.mock.request))

(comment
  (ns test-groops.web)
  (require '[groops.web :as web])
  (use 'clojure.test)
  (use 'ring.mock.request))

(def basic-req {:get "/"})

(deftest landing-template-not-blank
  (is (< 0 (count (web/landing-page basic-req)))))

(deftest landing-template-contains-groops-js
  (is (some #(.contains % "groops.js") (web/landing-page basic-req))))

(deftest root-route-okay
  (is (= 200 (:status (web/app-routes (request :get "/"))))))
