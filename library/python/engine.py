#!/usr/bin/python
# Copyright (c) 2013, Swedish Institute of Computer Science
# All rights reserved.
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions are met:
#   * Redistributions of source code must retain the above copyright
#     notice, this list of conditions and the following disclaimer.
#   * Redistributions in binary form must reproduce the above copyright
#     notice, this list of conditions and the following disclaimer in the
#     documentation and/or other materials provided with the distribution.
#   * Neither the name of The Swedish Institute of Computer Science nor the
#     names of its contributors may be used to endorse or promote products
#     derived from this software without specific prior written permission.
#
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
# ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
# WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
# DISCLAIMED. IN NO EVENT SHALL THE SWEDISH INSTITUTE OF COMPUTER SCIENCE BE LIABLE 
# FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
# (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
# LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
# ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
# (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
# SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

import urllib, urllib2
import simplejson as json
from websocket import create_connection

version = 0.2

class RequestWithMethod(urllib2.Request):
  def __init__(self, method, *args, **kwargs):
    self._method = method
    urllib2.Request.__init__(self, *args, **kwargs)

  def get_method(self):
    return self._method


class Engine:
	"""Object for interacting with the SicsthSense Engine"""
	#hostname = "http://sense.sics.se:8080" 
	hostname = "localhost:8080" 

	def __init__(self, userId=None):
		self.setUser(userId)

	def registerUser(userJSON):
		pass
	def updateUser(userId, userJSON):
		pass
	def deleteUser(userId):
		pass

	# Resource CRUD
	def createResource(self, resourceJSON):
		url = "http://"+self.hostname+"/users/"+str(self.userId)+"/resources/"
		return self.postToURL(url, resourceJSON)
	
	def updateResource(self, resourceId, resourceJSON):
		url = "http://"+self.genResourceURL(resourceId)
		return self.putToURL(url, resourceJSON)

	def deleteResource(self, resourceId):
		url = "http://"+self.genResourceURL(resourceId)
		return self.deleteURL(url)


	# Stream CRUD
	def createStream(self, resourceId, streamJSON):
		url = "http://"+self.genResourceURL(resourceId)+"/streams"
		return self.postToURL(url, streamJSON)
		
	def updateStream(self, resourceId, streamId, streamJSON):
		url = "http://"+self.genStreamURL(resourceId, streamId)
		return self.putToURL(url, streamJSON)
	
	def deleteStream(self, resourceId, streamId):
		url = "http://"+self.genStreamURL(resourceId, streamId)
		return self.deleteURL(url)


	# Parser CRUD
	def createParser(self, resourceId, parserJSON):
		url = "http://"+self.genResourceURL(resourceId)+"/parsers"
		return self.postToURL(url, parserJSON)

	def updateParser(self, resourceId, parserId, parserJSON):
		url = "http://"+self.genParserURL(resourceId, parserId)
		return self.putToURL(url, parserJSON)

	def deleteParser(self, resourceId, parserId):
		url = "http://"+self.genParserURL(resourceId, parserId)
		return self.deleteURL(url)


	# Parser & Stream CRUD sugar
	def createStreamParser(self, resourceId, streamParserJSON):
		self.createParser(resourceId, streamParserJSON)
		#self.createStream(resourceId, streamParserJSON)



	# Data posting 
	def postResourceData(self, resourceId, value, time=None):
		url = "http://"+self.genResourceURL(resourceId)+"/data"
		return self.postToURL(url, value)

	def postStreamData(self, resourceId, streamId, value, time=None):
		url = "http://"+self.genStreamURL(resourceId, streamId)+"/data"
		return self.postToURL(url, value)

	# generate a websocket for posting to a specific stream
	def genWebsocketPost(self, resourceId, streamId):
		url = "ws://"+self.genStreamURL(resourceId, streamId)+"/ws"
		ws = create_connection(url)
		return ws

	# generate a websocket for posting to a specific stream
	def genWebsocketGet(self, resourceId, streamId):
		url = "ws://"+self.genStreamURL(resourceId, streamId)+"/ws"
		ws = create_connection(url)
		return ws


	# GET data
	def getStreamData(self, resourceId, streamId, count=10):
		url = "http://"+self.genStreamURL(resourceId, streamId)+"/data"
		return self.getFromURL(url)



	#
	# Utility methods
	#

	# Build the URL to the given resource, using the hostname and userId
	def genResourceURL(self, resourceId):
		return self.hostname+"/users/"+str(self.userId)+"/resources/"+str(resourceId)

	# Build the URL to the given stream, using the hostname, userId and resourceId
	def genStreamURL(self, resourceId, streamId):
		return self.genResourceURL(resourceId)+"/streams/"+str(streamId)

	# Build the URL to the given stream, using the hostname, userId and resourceId
	def genParserURL(self, resourceId, parserId):
		return self.genResourceURL(resourceId)+"/parsers/"+str(parserId)

	# Ensure hostname and userId are set
	def valid(self):
		if self.userId==None:
			return False
		return True

	# Do client side verification of JSON to send
	def validJSON(self, data):
		return True

	def getFromURL(self,url):
		if not self.valid():
			return "Engine configuration not valid!"
		print "url: "+url
		try:
			req = urllib2.Request(url)
			response = urllib2.urlopen(req)
		except Exception as e:
			print "Connection to "+url+" failed\n!",e
			return None
		# check response was 20X
		return response.read()


	def postToURL(self,url,data):
		if not self.valid():
			return "Engine configuration not valid!"
		if not self.validJSON(data):
			print "JSON not valid!:\n"+data
			return False
		print "url: "+url
		headers = {'Content-Type':'application/json'}
		try:
			req = urllib2.Request(url, data, headers)
			response = urllib2.urlopen(req)
		except Exception as e:
			print "Error: Connection to "+url+" failed!\n",e
			return None
		# check response code
		if response.getcode() > 400:
			print "Error: HTTP return code "+response.getcode()
			print response.info()
		return response.read()

	# DELETE data
	def deleteURL(self, url):
		if not self.valid():
			return "Engine configuration not valid!"
		print "DELETE url: "+url
		try:
			req2 = RequestWithMethod("DELETE",url)
			response = urllib2.urlopen(req2)
		except Exception as e:
			print "Connection to "+url+" failed\n!",e
			return None
		# check response was 20X
		return response.read()
	
	# PUT data
	def putToURL(self, url, data):
		if not self.valid():
			return "Engine configuration not valid!"
		print "PUT url: "+url
		headers = {'Content-Type':'application/json'}
		try:
			req2 = RequestWithMethod("PUT", url, data, headers)
			response = urllib2.urlopen(req2)
		except Exception as e:
			print "Connection to "+url+" failed\n!",e
			return None
		# check response was 20X
		return response.read()
	


	#
	# Getters and Setters
	#
	def setHostname(self,hostname):
		self.hostname = hostname

	def setUser(self,userId):
		self.userId = userId


