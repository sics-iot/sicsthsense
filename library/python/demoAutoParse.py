#!/usr/bin/python
import simplejson as json
import random
import time
from Engine import *

print "Testing SicsthSense python module and autocreation of Streams and Parsers"

# Create the ENgine module giving the running instance's hostname and port
e = Engine("localhost:8080")
#e = Engine("presense.sics.se:8080")
print "Hostname: "+e.hostname

registerNewUser=False
if registerNewUser:
    # generate a random username
    username = "newuser"+str(random.randint(0,99))

    # register with SicsthSense
    newUserJSONStr = e.registerUser('{"username": "'+username+'", "email":"'+username+'@anon.com"}')

    # parse the returned JSON
    newUserJSON = json.loads(newUserJSONStr)

    # record our unique user ID
    newUserId = newUserJSON["id"]

    # record the new authentication token/key we have been given
    key = newUserJSON["token"]
else:
    # User existing user details
    newUserId = 1
    key = "4cb13dfc-b34e-4411-982b-2a0137162aa1"

# Configure our user ID and user Key
e.setUserId(newUserId)
e.setKey(key)
print "User ID:",newUserId," key: ",key

## Create a resource

# Generate random label name
resourceLabel = "demo"+str(random.randint(0,99))
# Create the new Resource's minimal JSON representation
newresource = {"label": resourceLabel}
jsonstr = json.dumps(newresource)
print "Resource JSON: "+jsonstr

# Create the new resource with the Engine
resourceId = e.createResource(jsonstr)
print "Made resource ID: "+str(resourceId);

# Use auto creation of streams and parsers
autoCreateStreams=True
if autoCreateStreams:
    # generate a random stream name
    tempname = "temperature"+str(random.randint(0,20)) 

    # generate 10 random data points
    for x in range(0,10):
        # craft a JSON payload
        data = { tempname : random.randint(0,20) }
        datastr = json.dumps(data)
        print "Sending....",datastr
        # Post this data to SicsthSense
        result = e.postResourceData(resourceId,json.dumps(data))
        print "Posted: "+result
        time.sleep(2)



# GET data
if False:
    result = e.getStreamData(resourceId,streamId)
    print json.dumps(json.loads(result), sort_keys = False, indent = 4)

# delete resource and everything (streams and parsers) under it
wipeData=False
if wipeData:
    print "Now deleting it all..."
    e.deleteResource(resourceId)


