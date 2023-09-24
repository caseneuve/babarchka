(ns tests.test-config-file
  (:require
   [babashka.fs :as fs]
   [clojure.test :refer [deftest is testing use-fixtures]]
   [flatland.ordered.map :refer [ordered-map]]
   [src.config-file :as cf]
   ))

(def ^:dynamic *cfg-file* nil)

(defn cfg-file-fixture [f]
  (binding [*cfg-file* (fs/create-temp-file)]
    (try
      (f)
      (finally (fs/delete-if-exists *cfg-file*)))))

(use-fixtures :once cfg-file-fixture)

(deftest read-config
  (testing "throws when file does not exist"
    (is (thrown? Exception (cf/read-config "/foo/bar"))))
  (testing "throws when file contains not yaml-compatible data"
    (fs/write-bytes *cfg-file* (.getBytes "[foo]\nbar"))
    (is (thrown? Exception (cf/read-config (str *cfg-file*)))))
  (testing "returns properly parsed data structures from yaml"
    (fs/write-bytes *cfg-file* (.getBytes "# irrelevant\n---\n- foo: bar\n  baz: quux"))
    (is (= [(ordered-map :foo "bar" :baz "quux" )] (cf/read-config (str *cfg-file*))))))
