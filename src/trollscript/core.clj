(ns trollscript.core
  (:use [clojure.string :only [lower-case]])
  (:require [trollscript.interpret :as ti])
  (:gen-class))

(defn interpret
  "Start the interpreter"
  [script]
  (ti/interpret (lower-case script)))

(defn -main [script & args]
  (interpret script))
