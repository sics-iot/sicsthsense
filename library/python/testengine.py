#!/usr/bin/python
import simplejson as json

from engine import *

print "Testing SicsthSense python module..."

e = Engine("1")
print e.hostname
#e.setUser("1")

result = e.getStreamData("1","1")
print json.dumps(json.loads(result), sort_keys = False, indent = 4)

result = e.postStreamData("1","1",'{ "value":"3113" }')
print result
#print json.dumps(json.loads(result), sort_keys = False, indent = 4)


