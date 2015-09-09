;; Shift - Cmd - L: Load file
;; Shift - Cmd - P: Load top-level form under caret

(ns visualise-warmup
    (:require
      [cl-java-introspector.spring :refer :all]
      [cl-java-introspector.core :refer :all]
      [alembic.still :refer [distill]]))

(use '(incanter core charts datasets))

(def renderer (get-bean "jsRenderer"))
(def stats (into [] (-> renderer .getLastWarmupResult .getStats)))

;(view (doto (xy-plot (range (length stats)) stats)
;            (set-axis :y (log-axis :base 10))))

(view (doto (xy-plot (range (length stats)) stats)
            (set-y-range 0 50)
            (set-y-label "Time (ms)")
            (set-x-label "Iterations")))

;(view (xy-plot (range (length stats)) stats))

;(view (doto (trace-plot stats)
;            (set-axis :y (log-axis :base 10))))
