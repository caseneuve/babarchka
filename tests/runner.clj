(ns tests.runner
  "Simple test runner based on `clojure.test` providing ability to chose tests
  by name or mark"
  (:require [clojure.test :as test]
            [clojure.string :as s]
            [babashka.fs :as fs]))

(defmethod test/report :begin-test-ns [m]
  (test/with-test-out
    (println
     "\nTesting " (ns-name (:ns m))
     (if-let [m (:matcher m)] (format " tests matching %s" m) ""))))

(defmethod test/report :begin-test-var [m]
  (test/with-test-out (printf " • %s " (-> m :var meta :name))))

(defmethod test/report :end-test-var [_]
  (test/with-test-out (print "\n")))

(defmethod test/report :pass [_]
  (test/with-test-out
    (test/inc-report-counter :pass)
    (print ".")))

(defn test-some-vars
  [ns vars matcher]
  (binding [test/*report-counters* (ref test/*initial-report-counters*)]
    (let [ns-obj (the-ns ns)]
      (test/do-report {:type :begin-test-ns, :ns ns-obj, :matcher matcher})
      (test/test-vars vars)
      (test/do-report {:type :end-test-ns, :ns ns-obj}))
    @test/*report-counters*))

(defn run-matching-tests [ns {:keys [mark test-name] :as m}]
  (let [pub (ns-publics ns),
        [tests matcher]
        (cond
          mark [(filter #((keyword mark) (meta %)) (vals pub)),
                (select-keys m [:mark])]
          test-name [(keep (fn [[k v]] (when (s/includes? k test-name) v)) pub),
                     (select-keys m [:test-name])]
          :else [])]
    (if (empty? tests) {}
        (test-some-vars ns tests matcher))))

(defn run-all-matching-tests [namespaces matchers]
  (let [results (for [ns namespaces] (run-matching-tests ns matchers))
        result (apply merge-with + results)
        summary (assoc result :type :summary)]
    (test/do-report summary)))

(defn discover-namespaces [matcher]
  (let [nsx (->> (fs/list-dir "./tests")
                 (map fs/file-name)
                 (filter #(re-find (re-pattern (or matcher ".*")) %))
                 (filter #(s/starts-with? % "test"))
                 (map #(s/replace % #"_|\.clj" {"_" "-" ".clj" ""}))
                 (map #(str "tests." %))
                 (map symbol))]
    (apply require nsx)
    nsx))

(defn -main [{:keys [error test-name mark namespace] :as args}]
  (let [nsx (discover-namespaces namespace)]
    (cond
      error (println error)
      (or mark test-name) (run-all-matching-tests nsx (select-keys args [:mark :test-name]))
      :else (apply test/run-tests nsx))))
