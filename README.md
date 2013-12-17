# client-oauth2

A Clojure Oauth client library. 

This library provides implementation of two specific oauth workflows - 

1. [Authenticated Request Oauth 1.0](http://tools.ietf.org/html/rfc5849#page-14). 
2. [Client Credentials Grant Oauth 2.0](http://tools.ietf.org/html/rfc6749#section-4.4) Workflow. 

## Usage

To use the Oauth 1.0 Authenticated Request flow - 

```clojure
(:require [client-oauth2.oauth-header :as oauth])

(def options
  {:url "https://api.twitter.com/1.1/statuses/user_timeline.json"
   :method :get
   :query-params {:count 200}
   :form-params {:a "abc"}})

(oauth/get-auth-header 
	:consumer-key "xxxxxxxxxxxxxxxxxxxxx"
    :consumer-secret "xxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
    :request-options options
    :oauth-token "xxxxxxxxxxxxxxxxxxxxx"
    :oauth-token-secret "xxxxxxxxxxxxxxxxxxxxx")

;; You can also access the intermediate results used for creating the auth header. This is useful for debugging.

(oauth/get-auth-header-graph)

```

## Reference

The following twitter documentation was an invaluable resource in coding the above flows, and the code is based on it - 

1. [Authenticated Request flow](https://dev.twitter.com/docs/auth/authorizing-request) 
2. [Client Credentials Grant](https://dev.twitter.com/docs/auth/application-only-auth)

Kudos to the twitter folks for taking the efforts to publish such detailed information !

## To Do 

1. Add some unit tests.
2. Exception handling.

## License

Copyright © 2013 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
