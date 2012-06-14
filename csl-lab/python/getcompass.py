#!/usr/bin/python
import http, sys, json

# get the latest value from one of the streams at Joakim's COSM account
# configuration for the account
# host = [2001:6b0:3a:1:211d:384b:8968:3cc4]
host = "sense.sics.se"
user = "simon"
device = "phone"
sensor = "sensors/compass"

# first argument used as sensor id if any
if len(sys.argv) > 1:
    sensor = sys.argv[1]

# fetch the data from sense.se
path = "/streams/%s/%s/%s?tail=5"%(user, device, sensor)
print "Fetching last 5 values from", path
[res, data] = http.get(host, path)
print res.status
print data
