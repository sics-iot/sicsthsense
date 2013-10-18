#!/usr/bin/python
import urllib, urllib2
import simplejson as json

version = 0.1

class RequestWithMethod(urllib2.Request):
  def __init__(self, method, *args, **kwargs):
    self._method = method
    urllib2.Request.__init__(self, *args, **kwargs)

  def get_method(self):
    return self._method


class Engine:
	"""Object for interacting with the SicsthSense Engine"""
	#hostname = "http://sense.sics.se:8080" 
	hostname = "http://localhost:8080" 

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
		url = self.hostname+"/users/"+str(self.userId)+"/resources/"
		return self.postToURL(url,resourceJSON)
	
	def updateResource(self, resourceId, resourceJSON):
		url = self.genResourceURL(resourceId)
		return self.postToURL(url,resourceJSON)

	def deleteResource(self, resourceId):
		url = self.genResourceURL(resourceId)
		return self.deleteURL(url)


	# Stream CRUD
	def createStream(self, resourceId, streamJSON):
		url = self.genResourceURL(resourceId)+"/streams"
		return self.postToURL(url,streamJSON)
		
	def updateStream(self, resourceId, streamId, streamJSON):
		url = self.genStreamURL(resourceId, streamId)
		return self.postToURL(url,streamJSON)
	
	def deleteStream(self, resourceId, streamId):
		url = self.genStreamURL(resourceId, streamId)
		return self.deleteURL(url)


	# Parser CRUD
	def createParser(self, resourceId, parserJSON):
		url = self.genResourceURL(resourceId)+"/parsers"
		return self.postToURL(url,parserJSON)

	def deleteParser(self, resourceId, parserId):
		url = self.genParserURL(resourceId, parserId)
		return self.deleteURL(url)


	# Parser & Stream CRUD sugar
	def createStreamParser(self, resourceId, streamParserJSON):
		self.createParser(resourceId, streamParserJSON)
		#self.createStream(resourceId, streamParserJSON)



	# Data posting 
	def postResourceData(self, resourceId, value, time=None):
		url = self.genResourceURL(resourceId)+"/data"
		return self.postToURL(url,value)

	def postStreamData(self, resourceId, streamId, value, time=None):
		url = self.genStreamURL(resourceId, streamId)+"/data"
		return self.postToURL(url,value)


	# Data getting

	def getStreamData(self, resourceId, streamId, count=10):
		url = self.genStreamURL(resourceId, streamId)+"/data"
		return self.getFromURL(url)

	# Delete data
	def deleteURL(self, url):
		if not self.valid():
			return "Engine configuration not valid!"
		print "url: "+url
		try:
			req2 = RequestWithMethod("DELETE",url)
			response = urllib2.urlopen(req2)
		except Exception as e:
			print "Connection to "+url+" failed\n!",e
			return None
		# check response was 20X
		return response.read()
	


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


	#
	# Getters and Setters
	#
	def setHostname(self,hostname):
		self.hostname = hostname

	def setUser(self,userId):
		self.userId = userId


