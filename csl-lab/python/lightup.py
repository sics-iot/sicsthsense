#!/usr/bin/python
import http, sys, json, time

def leds_on(host):
    postData = '{"leds":7}'
    [res, data] = http.post(host, "/act", postData)

def leds_off(host):
    postData = '{"leds":0}'
    [res, data] = http.post(host, "/act", postData)

# Python code for switching LEDs based on light level
host = sys.argv[1]
# Assume that the LEDs are off at the beginning
# [TASK: add a read of the LED state to get it correct at startup]
leds = 0
while 1:
    print "Reading sensor data..."
    [res, data] = http.get(host, "/sen")
    print "Data:", data
    jData = json.loads(data)
    if 'light' in jData:
        if leds > 0:
            if jData['light'] > 90:
                leds_off(host)
                leds = 0
        elif jData['light'] < 90:
                leds_on(host)
                leds = 1
    print "Sleeping..."
    time.sleep(120)
