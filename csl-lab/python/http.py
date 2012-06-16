import httplib,sys

def post(host, path, postData, headers={}):
    conn = httplib.HTTPConnection(host)
    print "Posting data:",postData,"to",path
    conn.request("POST", path, postData, headers)
    res = conn.getresponse();
    # read out data to ensure that the connection is not killed too early
    # (uIP does not handle that correctly at the moment
    data = res.read();
    res.close();
    return [res, data]

def get(host, path, headers={}):
    conn = httplib.HTTPConnection(host)
    conn.request("GET", path, "", headers)
    res = conn.getresponse()
    data = res.read()
    res.close()
    return [res, data]
