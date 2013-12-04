(ns clj-oauth2.core
    (:require [clj-oauth2.client-grant :refer [get-access-token]]))

(def twitter-uri "https://api.twitter.com/oauth2/token")

(def get-twitter-token (partial get-access-token twitter-uri))


;; schema
;; ribol
;; comments based on RFC
;; tests

