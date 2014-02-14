#!/usr/bin/python

import subprocess
import time
import Engine

#
# Set your userID and key here!
#
key = "4cb13dfc-b34e-4411-982b-2a0137162aa1"
newUserId = -1
resourceId= -1


# Create the ENgine module giving the running instance's hostname and port
e = Engine("localhost:8080")
#e = Engine("presense.sics.se:8080")
print "Hostname: "+e.hostname
# Configure our user ID and user Key
e.setUserId(newUserId)
e.setKey(key)

makeNewResource=True
if makeNewResource:
    # Generate random label name
    resourceLabel = "uptime"+str(random.randint(0,99))

    # Create the new Resource's minimal JSON representation
    newresource = {"label": resourceLabel}
    jsonstr = json.dumps(newresource)
    print "Resource JSON: "+jsonstr

    # Create the new resource with the Engine
    resourceId = e.createResource(jsonstr)
    print "Made resource ID: "+str(resourceId);

running=True
while running:
    # Capture the output of `uptime`
    upstring = subprocess.check_output("uptime")
    #print upstring

    # extract the CPU load measurements
    upfields = upstring.split()
    avg1 =upfields[9]
    avg5 =upfields[10]
    avg15=upfields[11]

    data = { "avg1":avg1, "avg5":avg5, "avg15":avg15}
    datastr = json.dumps(data)
    print "Posting....",datastr

    # Post this data to SicsthSense
    result = e.postResourceData(resourceId,json.dumps(data))
    print "Posted: "+result



