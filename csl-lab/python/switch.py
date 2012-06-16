#!/usr/bin/python
# Python code for switching the H&D Wireless WiFi power plugs.
# Usage: switch.py IP-Host [0|1]

import http, sys, re

host = sys.argv[1]
state = int(sys.argv[2])

if state > 0:
    state = "On"
else:
    state = "Off"

[res, data] = http.get(host, "/index.html?p0=%s"%(state))
newstate = re.search('Socket 1</td><td>(.+?)</td>', data)
power = re.search('(\d+\.\d+) W</td>', data)
print "Power:", power.group(1), " State:", newstate.group(1)
