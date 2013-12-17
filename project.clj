(defproject client-oauth2 "0.1.8"
  :description "A oauth2 library for clojure."
  :url "https://github.com/murtaza52/clj-oauth2"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :scm {:name "git"
        :url "https://github.com/murtaza52/clj-oauth2"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [http-kit "2.1.13"]
                 [base64-clj "0.1.1"]
                 [org.clojure/data.json "0.2.3"]
                 [crypto-random "1.1.0"]
                 [ring/ring-codec "1.0.0"]
                 [clj-time "0.6.0"]
                 [pandect "0.3.0"]
                 [prismatic/plumbing "0.1.1"]])
