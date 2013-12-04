(ns clj-oauth2.client-grant
  (:require [base64-clj.core :refer [encode]]
            [org.httpkit.client :as http]
            [clojure.data.json :as json]))

(defn basic-auth-header
  [user pass]
  (->> (str user ":" pass)
       encode
       (str "Basic ")))

(defn request-options
  [auth-header]
  {:headers {"Authorization" auth-header
             "Content-Type" "application/x-www-form-urlencoded;charset=UTF-8"}
   :form-params {"grant_type" "client_credentials"}})

(def token-type-value "bearer")

(defn process-response
  [resp-body]
  (let [resp (json/read-str resp-body)]
        (when (= (resp "token_type") token-type-value)
          (resp "access_token"))))

(defn http-post
  [f]
  (fn [uri options]
    (let [{:keys [body error]} @(http/post uri options)]
      (when-not error
        (f body)))))

(def request-token (http-post process-response))

(defn get-access-token
  [uri user pass]
  (let [auth-header (basic-auth-header user pass)
        options (request-options auth-header)]
    (request-token uri options)))
