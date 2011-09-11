(ns trollscript.core
  (:use [clojure.string :only [lower-case]])
  (:gen-class))

;(def in (input-stream *in*))
(def MAX_CELLS 30000)

(defn split-code
  "Splits the string into its trollscript commands"
  [raw-code]
  (->> raw-code
    (partition 3 3 nil)
    (map #(apply str %))))

(defn basic-validation
  "Perform some basic validation to make sure the code is formatted correctly"
  [code]
  (let [f (first code)
        l (last code)]
    (and 
      (= "tro" f)
      (= "ll." l))))

(defn get-input
  "Retrieve a byte from the input"
  [data dp]
  (let [new-data (.charAt read-line 0)]
    (assoc data dp new-data)))

(defn change-data
  "Apply f to the byte at the data pointer"
  [f dp data]
  (let [current-value (data dp 0)]
    (assoc data dp (mod (f current-value) 256))))

(defn output-data
  "Output the char of the byte at the data pointer"
  [data dp]
  (let [value (char (data dp 0))]
    (print value)
    data))

(def jump-forward
  (memoize
    (fn [code pc]
      (loop [new-pc pc depth 0]
        (let [command (nth code new-pc)]
          (case command
            "llo" ;Increase depth
            (recur (inc new-pc) (inc depth))
            "lll" ;Decrease depth
            (if (zero? (dec depth))
              (inc new-pc)
              (recur (inc new-pc) (dec depth)))
            (recur (inc new-pc) depth)))))))

(defn do-jump-forward
  "Determine if a jump forward is required"
  [code pc data dp]
  (if (zero? (data dp 0))
    (jump-forward code pc)
    (inc pc)))

(def jump-back
  (memoize
    (fn [code pc]
      (loop [new-pc pc depth 0]
        (let [command (nth code new-pc)]
          (case command
            "lll" ;Increase depth
            (recur (dec new-pc) (inc depth))
            "llo" ;Decrease depth
            (if (zero? (dec depth))
              (inc new-pc)
              (recur (dec new-pc) (dec depth)))
            (recur (dec new-pc) depth)))))))

(defn do-jump-back
  "Determine if a jump back is require"
  [code pc data dp]
  (if (not (zero? (data dp 0)))
    (jump-back code pc)
    (inc pc)))

(defn interpret
  "Interpret the trollscript"
  [raw-code]
  (let [code (split-code raw-code)]
    (if (basic-validation code)
      (loop [data {} pc 0 dp 0 cycles 0]
        (case (nth code pc)
          "tro" ;Begining of the script
          (recur data (inc pc) dp cycles)
          "ll." ;End of the script
          (println "\nEND took" cycles "cycle(s) to execute")
          "ooo" ;Increment the data pointer
          (recur data (inc pc) (min (inc dp) MAX_CELLS) (inc cycles))
          "ool" ;Decrement the data pointer
          (recur data (inc pc) (max (dec dp) 0) (inc cycles))
          "olo" ;Increment the byte at the data pointer
          (recur (change-data inc dp data) (inc pc) dp (inc cycles))
          "oll" ;Decrement the byte at the data pointer
          (recur (change-data dec dp data) (inc pc) dp (inc cycles))
          "loo" ;Output a character (ASCII of byte at data pointer)
          (recur (output-data data dp) (inc pc) dp (inc cycles))
          "lol" ;Accept one byte of input (stored at data pointer)
          (recur (get-input data dp) (inc pc) dp (inc cycles))
          "llo" ;Jump forwared on zero to matching lll
          (recur data (do-jump-forward code pc data dp) dp (inc cycles))
          "lll" ;Jump back on non-zero to matching llo
          (recur data (do-jump-back code pc data dp) dp (inc cycles))
          (println "INVALID COMMAND")))
      "Malformed trollscript")))

(defn -main [script & args]
  (interpret (lower-case script)))
