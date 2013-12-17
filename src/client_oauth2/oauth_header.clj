(ns client-oauth2.oauth-header
  (:require [crypto.random :as crypt-rand]
            [ring.util.codec :as codec]
            [clj-time.core :as joda-time]
            [clj-time.coerce :as joda-coerce]
            [clojure.string :as string]
            [pandect.core :as pandect]
            [plumbing.core :refer [fnk]]
            [plumbing.graph :as graph]))

(def get-nonce #(crypt-rand/base64 32))

;; the timestamp in millisecs from epoch time in GMT
(def get-timestamp #(-> (joda-time/now) joda-coerce/to-long (quot 1000)))

(defn percent-encode
  "Percent encode `unencoded` according to RFC 3986, Section 2.1.
  src taken from - https://github.com/r0man/oauth-clj/blob/master/src/oauth/util.clj"
  [unencoded]
  (-> (codec/url-encode (str unencoded))
      (string/replace "%7E" "~")
      (string/replace "*" "%2A")
      (string/replace "+" "%2B")))

(defn- get-oauth-params
  [nonce timestamp consumer-key oauth-token]
  {:oauth_consumer_key consumer-key
   :oauth_nonce nonce
   :oauth_signature_method "HMAC-SHA1"
   :oauth_timestamp timestamp
   :oauth_token oauth-token
   :oauth_version "1.0"})

(defn- create-parameter-string
  [oauth-params request-options]
  (->>(merge oauth-params (request-options :query-params) (request-options :form-params))
      (map (fn [[k v]] [(if (keyword? k) (name k) k) v]))
      (map (fn [[k v]] [(percent-encode k) (percent-encode v)]))
      sort
      (reduce (fn [param-string [k v]] (if (string/blank? param-string)
                                         (str k "=" v)
                                         (str param-string "&" k "=" v))) "")))

(defn- create-signature-base-string
  [parameter-string request-options]
  (str
     (-> request-options :method name string/upper-case)
     "&"
     (-> request-options :url percent-encode)
     "&"
     (percent-encode parameter-string)))

(defn- create-signing-key
  [consumer-secret oauth-token]
  (str
   (percent-encode consumer-secret)
   "&"
   (percent-encode oauth-token)))

(defn- create-oauth-signature
  [message signing-key]
  (-> (pandect/sha1-hmac-bytes message signing-key)
      codec/base64-encode))

(defn- create-oauth-header
  [oauth-params oauth-signature]
  (let [p (assoc oauth-params :oauth_signature oauth-signature)]
    (str "OAuth "
         (reduce (fn [header-string [k v]] (if (string/blank? header-string)
                                             (str (name k) "=" (percent-encode v))
                                             (str header-string ", " (name k) "=" (percent-encode v)))) "" p))))

(def auth-header-graph
  (graph/eager-compile
    (graph/graph
     {:nonce (fnk [] (get-nonce))
      :timestamp (fnk [] (get-timestamp))
      :oauth-params (fnk [consumer-key oauth-token nonce timestamp] (get-oauth-params nonce timestamp consumer-key oauth-token))
      :parameter-string (fnk [oauth-params request-options] (create-parameter-string oauth-params request-options))
      :signature-base-string (fnk [parameter-string request-options] (create-signature-base-string parameter-string request-options))
      :signing-key (fnk [consumer-secret oauth-token-secret] (create-signing-key consumer-secret oauth-token-secret))
      :oauth-signature (fnk [signature-base-string signing-key] (create-oauth-signature signature-base-string signing-key))
      :auth-header (fnk [oauth-params oauth-signature] (create-oauth-header oauth-params oauth-signature))
      })))

;; helpful in debugging as the whole graph is returned with intermediate results of calculating the auth-header
(defn get-auth-header-graph
  [& {:keys [consumer-key consumer-secret request-options oauth-token oauth-token-secret] :as params}]
  (auth-header-graph params))

(defn get-auth-header
  [& {:keys [consumer-key consumer-secret request-options oauth-token oauth-token-secret] :as params}]
  (-> (auth-header-graph params)
      :auth-header))
