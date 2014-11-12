#!/usr/bin/python
import simplejson as json
import random
import time
from engine import *

print "Testing SicsthSense python module with manual creation of Streams and Parsers"

# Create the ENgine module giving the running instance's hostname and port
e = Engine("localhost:8080")
#e = Engine("presense.sics.se:8080")
print "Hostname: "+e.hostname

registerNewUser=False
if registerNewUser:
    # generate a random username
    username = "newuser"+str(random.randint(0,999))

    # register with SicsthSense
    newUserJSONStr = e.registerUser('{"username": "'+username+'", "email":"'+username+'@anon.com", "password":"password"}')

    # parse the returned JSON
    newUserJSON = json.loads(newUserJSONStr)

    # record our unique user ID
    newUserId = newUserJSON["id"]

    # record the new authentication token/key we have been given
    key = newUserJSON["token"]
else:
    # User existing user details
    newUserId = 1
    key = "63660edf-a699-413c-9363-325ba66bbe2f"

# Configure our user ID and user Key
e.setUserId(newUserId)
e.setKey(key)
print "UserID:",newUserId," key: ",key+"\n"

## Create a resource
# Generate random label name
resourceLabel = "demo"+str(random.randint(0,999))
# Create the new Resource's minimal JSON representation
newresource = {"label": resourceLabel}
jsonstr = json.dumps(newresource)
print "Resource JSON: "+jsonstr

# Create the new resource with the Engine
result= e.createResource(jsonstr)
#print result
resource = json.loads(result)
resourceId = resource['id']
print "Made resource ID: "+str(resourceId);


print "\nPosting initial data..."
data = {
    "acc_x": str(random.randint(0,9)),
    "acc_y": str(random.randint(0,9)),
    "acc_z": str(random.randint(0,9)),
    "gyro_x": str(random.randint(0,9)),
    "gyro_y": str(random.randint(0,9)),
    "gyro_z": str(random.randint(0,9)),
    "heartrate": str(random.randint(70,90))
}
result = e.postResourceData(resourceId,json.dumps(data))
#print str(data)+" -> "+str(result)
#print json.dumps(json.loads(result), sort_keys = False, indent = 4)
time.sleep(1)

result  = e.getResourceStreams(resourceId)
streams = json.loads(result)
#print "Streams: "+str(streams)
print "\nNew streams:"+str(streams)

antecedents = []
for stream in streams:
    antecedents.append(str(stream['id']))

intensityStream = {
    'function':'intensity',
    'label':'Intensity'
}
intensityStream['antecedents']=antecedents
print json.dumps(intensityStream)
result = e.createStream(resourceId,json.dumps(intensityStream))
print "\nIntensity stream: "+result
intensityObject = json.loads(result)
intensityId = intensityObject['id']

for i in range(5):
    print "\nPosting more data..."
    data = {
        "acc_x": str(random.randint(10,19)),
        "acc_y": str(random.randint(0,9)),
        "acc_z": str(random.randint(0,9)),
        "gyro_x": str(random.randint(0,9)),
        "gyro_y": str(random.randint(0,9)),
        "gyro_z": str(random.randint(0,9)),
        "heartrate": str(random.randint(70,90))
    }
    result = e.postResourceData(resourceId,json.dumps(data))
    print "Datapoint: "+json.dumps(data)
    result = e.getStreamData(resourceId,intensityId)
    print "Intensity: "+result
    time.sleep(1)

# delete resource and everything (streams and parsers) under it
wipeData=False
if wipeData:
    print "Now deleting it all..."
    e.deleteResource(resourceId)


