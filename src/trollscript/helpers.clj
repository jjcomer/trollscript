(ns trollscript.helpers)

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

(def jump-forward
  (memoize
    (fn [code pc]
      (loop [new-pc pc depth 0]
        (let [command (nth code new-pc)]
          (case command
            "llo" ;;Increase depth
            (recur (inc new-pc) (inc depth))
            "lll" ;;Decrease depth
            (if (zero? (dec depth))
              (inc new-pc)
              (recur (inc new-pc) (dec depth)))
            (recur (inc new-pc) depth)))))))

(def jump-back
  (memoize
    (fn [code pc]
      (loop [new-pc pc depth 0]
        (let [command (nth code new-pc)]
          (case command
            "lll" ;;Increase depth
            (recur (dec new-pc) (inc depth))
            "llo" ;;Decrease depth
            (if (zero? (dec depth))
              (inc new-pc)
              (recur (dec new-pc) (dec depth)))
            (recur (dec new-pc) depth)))))))
