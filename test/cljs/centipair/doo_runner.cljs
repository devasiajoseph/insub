(ns centipair.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [centipair.core-test]))

(doo-tests 'centipair.core-test)

