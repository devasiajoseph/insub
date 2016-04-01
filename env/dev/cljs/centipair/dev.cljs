(ns ^:figwheel-no-load centipair.app
  (:require [centipair.core :as core]
            [figwheel.client :as figwheel :include-macros true]))

(enable-console-print!)

(figwheel/watch-and-reload
  :websocket-url "ws://localhost:3449/figwheel-ws")

(core/init!)
