#!/usr/bin/python
import http, sys, re

# Python code for switching LEDs
id = int(sys.argv[1])
[res, data] = http.get("hus2.so-ip.se:8%d"%(id), "")
if data:
    state = re.search('Socket 1</td><td>(.+?)</td>', data)
    power = re.search('(\d+\.\d+) W</td>', data)
    print "Power:", power.group(1), " State:", state.group(1)
else:
    print "No power plug found"
