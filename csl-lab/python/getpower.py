#!/usr/bin/python
# Python code for reading the H&D Wireless WiFi power plugs.
# Usage: getpower.py IP-Host

import http, sys, re

host = sys.argv[1]
[res, data] = http.get(host, "")
state = re.search('Socket 1</td><td>(.+?)</td>', data)
power = re.search('(\d+\.\d+) W</td>', data)
print "Power:", power.group(1), " State:", state.group(1)
