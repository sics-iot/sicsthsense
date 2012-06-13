#!/usr/bin/python
import http, sys, json

# get the latest value from one of the streams at Joakim's COSM account
# configuration for the account
key = "gd2-9t5MzIe7BrKG-cC1xOU_rFOSAKxTMDE2bGluYzVSST0g"
feed = "55180"
stream = "1"

# first argument used as stream id if any
if len(sys.argv) > 1:
   stream = sys.argv[1]

# fetch the data
[res, data] = http.get("api.cosm.com", "/v2/feeds/%s/datastreams/%s"%(feed,stream), {"X-ApiKey": key})
print data

