#!/usr/bin/python
import http, sys, re

# Python code for switching LEDs
id = int(sys.argv[1])
state = int(sys.argv[2])

if state > 0:
    state = "On"
else:
    state = "Off"

[res, data] = http.get("hus2.so-ip.se:8%d"%(id), "/index.html?p0=%s"%(state))
if data:
    newstate = re.search('Socket 1</td><td>(.+?)</td>', data)
    power = re.search('(\d+\.\d+) W</td>', data)
    print "Power:", power.group(1), " State:", newstate.group(1)
else:
    print "No power plug found"
