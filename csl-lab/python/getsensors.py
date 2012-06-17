#!/usr/bin/python
import http, sys, json

# Python code for getting the sensor data of a sensor
host = sys.argv[1]
[res, data] = http.get(host, "/sensors")
print "Sensors:", data

# parse json and do some stuff with data!
jData = json.loads(data)
if 'temperature' in jData and jData['temperature'] > 30:
    print "Temp is above 30 degrees - open window!"
