#!/usr/bin/python
import http, sys

# Python code for switching LEDs
host = sys.argv[1]
postData = '{"leds":%d}'%(int(sys.argv[2]))
[res, data] = http.post(host, "/act", postData)
print "HTTP Response:", res.status, res.reason, data
