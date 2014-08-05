(defproject faris/lune "0.1.0"
  :description "A http rest server that acts as a dynamic query tool."
  :url "https://github.com/farisnasution/lune.git"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/data.json "0.2.4"]
                 [instaparse "1.3.2"]
                 [ring "1.2.1"]
                 [ring/ring-json "0.3.0"]
                 [http-kit "2.1.16"]
                 [compojure "1.1.6"]
                 [juggler "0.2.4"]
                 [com.novemberain/monger "2.0.0"]
                 [liberator "0.11.0"]]
  :app-settings {:dev {:server {:ip "127.0.0.1"
                                :port 8080
                                :thread 4
                                :worker-name-prefix "worker-"
                                :queue-size 20000
                                :max-body 8388608
                               :max-line 4096}
                       :db {:host "127.0.0.1"
                            :port 27017
                            :db-name "default"}}})
