#!/usr/bin/python
import httplib,sys

def post(host, path, postData):
    conn = httplib.HTTPConnection(host)
    print "Posting data:",postData,"to",path
    conn.request("POST", path, postData)
    return conn.getresponse()

def get(host):
    conn = httplib.HTTPConnection(host)
    conn.request("GET","", NULL)
    return conn.getresponse()
    

# Python code for switching LEDs
host = sys.argv[1]
postData = '{"leds":%d}'%(int(sys.argv[2]))
res = post(host, "/rsc", postData)
print res.status, res.reason
