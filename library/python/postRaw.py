#!/usr/bin/python
import simplejson as json
import random
import time
from Engine import *

print "Testing SicsthSense python module..."

e = Engine("localhost:8080")
#e = Engine("presense.sics.se:8080")
print e.hostname

if False:
    username = "newuser"+str(random.randint(0,99))
    newUserJSONStr = e.registerUser('{"username": "'+username+'", "email":"'+username+'@anon.com"}')

    newUserJSON = json.loads(newUserJSONStr)
    newUserId = newUserJSON["id"]

    key = newUserJSON["token"]

newUserId = 1
key = "4cb13dfc-b34e-4411-982b-2a0137162aa1"
e.setUserId(newUserId)
e.setKey(key)
print "User ID:",newUserId," key: ",key

#exit(1);
# create a resource
resourceLabel = "demo"+str(random.randint(0,99))
#newresource = {"label": resourceLabel,"polling_url":"http://130.238.8.151:8888/test.json","polling_period":0}
newresource = {"label": resourceLabel}
jsonstr = json.dumps(newresource)
print "Resource JSON: "+jsonstr

resourceId = e.createResource(jsonstr)
print "Made resource ID: "+str(resourceId);

# Use auto creation of streams and parsers
if True:
    tempname = "temperature"+str(random.randint(0,20)) 
    for x in range(0,10):
        data = { tempname : random.randint(2010,2020) }
        datastr = json.dumps(data)
        print "Sending....",datastr
        result = e.postResourceData(resourceId,json.dumps(data))
        print "Posted: "+result
        time.sleep(2)


# Manually create stream and parser
if False:
        # Create streams in resource
	if False:
                print "Making streams..."
                newstream = { "description": "input1" }
		streamjsonstr = json.dumps(newstream)
		#print streamjsonstr
		streamId = e.createStream(resourceId,streamjsonstr)
		print "Made antecedent stream: "+str(streamId)+" - "+streamjsonstr;


	# Create a Parser for this resource
	if False:
		newparser = { "stream_id":streamId, "input_parser":"/tets" }
		parserjsonstr = json.dumps(newparser)
		#print parserjsonstr
		newId = e.createParser(resourceId,parserjsonstr)
		print "new parser ID: "+str(newId);
if False:
        print "Post data..."
        # POST data to made stream
        for x in range(0,10):
                #data = {"value": str(random.randint(0,99))}
                #result = e.postStreamData(resourceId,antStreamId1,json.dumps(data))

                data = {"value": str(random.randint(0,99))}
                result = e.postStreamData(resourceId,streamId,json.dumps(data))
                print str(data)+" -> "+str(result)
                #print json.dumps(json.loads(result), sort_keys = False, indent = 4)
                time.sleep(2)


# GET data
if False:
    result = e.getStreamData(resourceId,streamId)
    print json.dumps(json.loads(result), sort_keys = False, indent = 4)

# delete resource and everything (streams and parsers) under it
if False:
    print "Now deleting it all..."
    e.deleteResource(resourceId)


