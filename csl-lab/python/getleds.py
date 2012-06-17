#!/usr/bin/python
import http, sys

# Python code for switching LEDs
host = sys.argv[1]
[res, data] = http.get(host, "/actuators/leds")
print "LEDS:", data
