(ns testing
  (:require [clojure.test :as test]
            [clojure.string :as s]))

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

(defn run-matching-tests [ns {:keys [mark name] :as m}]
  (let [pub (ns-publics ns)
        [tests matcher]
        (cond
          mark [(filter #((keyword mark) (meta %)) (vals pub))
                (select-keys m [:mark])]
          name [(keep (fn [[k v]] (when (s/includes? k name) v)) pub)
                (select-keys m [:name])]
          :else [])
        summary (assoc (test-some-vars ns tests matcher) :type :summary)]
    (test/do-report summary)))
