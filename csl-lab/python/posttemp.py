#!/usr/bin/python
import http, sys

# Python code for sending a temperature values to sense.sics.se
host = "sense.sics.se"
user = "csl-lab"
device = "test-device"
temp = sys.argv[1]
postData = '{"temperature":%f}'%(float(temp))
[res, data] = http.post(host, "/streams/%s/%s/"%(user,device), postData,
                        {"Content-type":"application/json"})
print "HTTP Response:", res.status, res.reason, data
