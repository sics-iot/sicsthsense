#! /usr/bin/python

import http, json

class Device:
  
  _host = "sense.sics.se"
  _user = None
  _device = None
 
  def __init__(self, user, device):
    self._user = user
    self._device = device

  def path_string(self, path):
    return "/streams/" + self._user + "/" + self._device + ''.join(["/"+x for x in path])

  def post(self, path, value):
    [res, data] = http.post(self._host, self.path_string(path), json.dumps(value),
                        {"Content-type":"application/json"})
    print "HTTP Response:", res.status, res.reason, data

  def get(self, path, tail=-1, last=-1, since=-1):
    if (tail > -1):
      option = "?tail=" + str(tail)
    elif (last > -1):
      option = "?last=" + str(last)
    elif (since > -1):
      option = "?since=" + str(since)
    else:
      option = ""
    [res, data] = http.get(self._host, self.path_string(path)+option)
    print "HTTP Response:", res.status, res.reason
    return json.loads(data)

