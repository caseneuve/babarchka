(ns src.config-file
  (:require [clj-yaml.core :as yaml]))

(defn read-config [path]
  (-> path slurp yaml/parse-string))
