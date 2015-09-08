(ns visualise-warmup
    (:require
      [cl-java-introspector.spring :refer :all]
      [cl-java-introspector.core :refer :all]
      [alembic.still :refer [distill]]))

(def renderer (get-bean "jsRenderer"))

(use '(incanter core charts datasets))

(def stats (into [] (-> renderer .getLastWarmupResult .getStats)))

(view (doto (xy-plot (range (length stats)) stats)
            (set-axis :y (log-axis :base 10))))

(view (doto (xy-plot (range (length stats)) stats)
            (set-y-range 0 50)
            ()))

(view (xy-plot (range (length stats)) stats))

(view (doto (trace-plot stats)
            (set-axis :y (log-axis :base 10))))
