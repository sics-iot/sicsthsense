#!/usr/bin/python
import simplejson as json
import random
from engine import *

print "Testing SicsthSense python module..."

e = Engine("1")
print e.hostname
e.setUser("1")

# create a resource
resourceLabel = "demo"+str(random.randint(0,99))
newresource = {"label": resourceLabel,"polling_url":"http://130.238.8.151:8888/test.json","polling_period":30}
jsonstr = json.dumps(newresource)
#print jsonstr

resourceId = e.createResource(jsonstr)
print "Made resource: "+str(resourceId);

if True:
	if True:
		newstream = { "description": "light measure" }
		streamjsonstr = json.dumps(newstream)
		#print streamjsonstr
		streamId = e.createStream(resourceId,streamjsonstr)
		print "Made stream: "+str(streamId);

	# Create a stream for this resource
	if True:
		newparser = { "stream_id":streamId, "input_parser":"/tets" }
		parserjsonstr = json.dumps(newparser)
		#print parserjsonstr
		newId = e.createParser(resourceId,parserjsonstr)
		print "new parser ID: "+newId;


# POST data

for x in range(0,3):
	data = {"value": str(random.randint(0,99))}
	result = e.postStreamData(resourceId,streamId,json.dumps(data))
	print result
	#print json.dumps(json.loads(result), sort_keys = False, indent = 4)


# GET data

result = e.getStreamData(resourceId,streamId)
print json.dumps(json.loads(result), sort_keys = False, indent = 4)


if False:
	print "Now deleting it all..."
	e.deleteResource(resourceId)


