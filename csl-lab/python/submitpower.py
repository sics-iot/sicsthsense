#!/usr/bin/python
# Python code for reading the H&D Wireless WiFi power plugs.
# Usage: getpower.py IP-Host

import http, sys, re, time

hosts = ["hus2.so-ip.se:8110","hus2.so-ip.se:8111"]
sense = "sense.sics.se"
user = "csl-lab"

while 1:
    index = 0
    for host in hosts:
        [res, data] = http.get(host, "")
        newstate = re.search('Socket 1</td><td>(.+?)</td>', data)
        m = re.search('(\d+\.\d+) W</td>', data)
        power = m.group(1)
        state = newstate.group(1)
        if state == "Enabled":
            state = 1
        else:
            state = 0
        index = index + 1
        # Python code for sending a temperature values to sense.sics.se
        postData = '{"power":%f, "on":%d}'%(float(power),state)
        [res, data] = http.post(sense,
                                "/streams/%s/powerplug-%d/"%(user, index),
                                postData,{"Content-type":"application/json"})
        print "Updated %d with power = %f Status:"%(index, float(power)),
        res.status, res.reason
        time.sleep(10)
