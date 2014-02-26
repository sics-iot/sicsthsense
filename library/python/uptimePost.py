#!/usr/bin/python

import subprocess
import time
import random
from Engine import *

#
# Set your userID and key here!
#
key = "baf339d0-b565-4a14-a998-d14251a1844e"
newUserId = 1
resourceId= 39


# Create the ENgine module giving the running instance's hostname and port
#e = Engine("localhost:8080")
e = Engine("presense.sics.se:8080")
print "Hostname: "+e.hostname
# Configure our user ID and user Key
e.setUserId(newUserId)
e.setKey(key)

makeNewResource=False
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
    #print upfields
    avg1 =float(upfields[7])
    avg5 =float(upfields[8])
    avg15=float(upfields[9])

    data = { "avg1":avg1, "avg5":avg5, "avg15":avg15}
    datastr = json.dumps(data)
    print "Posting....",datastr

    # Post this data to SicsthSense
    result = e.postResourceData(resourceId,json.dumps(data))
    print "Posted: "+result

    time.sleep(10)


