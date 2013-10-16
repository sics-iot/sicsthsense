#!/usr/bin/python
import urllib, urllib2

version = 0.1

class Engine:
	"""Object for interacting with the SicsthSense Engine"""
	#hostname = "http://sense.sics.se:8080" 
	hostname = "http://localhost:8080" 

	def __init__(self, userId=None):
		self.setUser(userId)

	def postToResource(self, resourceId, value, time=None):
		if not self.valid():
			return "Engine configuration not valid!"
		url = self.genStreamURL(resourceId, streamId)+"/data"
		print "url: "+url
		headers = {'Content-Type':'application/json'}
		try:
			req = urllib2.Request(url, value, headers)
			response = urllib2.urlopen(req)
		except Exception as e:
			print "Error: Connection to "+url+" failed!\n",e
			return None
		# check response was 20X
		return response.read()

	def postStreamData(self, resourceId, streamId, value, time=None):
		if not self.valid():
			return "Engine configuration not valid!"
		url = self.genStreamURL(resourceId, streamId)+"/data"
		print "url: "+url
		headers = {'Content-Type':'application/json'}
		try:
			req = urllib2.Request(url, value, headers)
			response = urllib2.urlopen(req)
		except Exception as e:
			print "Error: Connection to "+url+" failed!\n",e
			return None
		# check response was 20X
		return response.read()

	def getStreamData(self, resourceId, streamId, count=10):
		if not self.valid():
			return "Engine configuration not valid!"
		url = self.genStreamURL(resourceId, streamId)+"/data"
		print "url: "+url
		try:
			req = urllib2.Request(url)
			response = urllib2.urlopen(req)
		except Exception as e:
			print "Connection to "+url+" failed\n!",e
			return None
		# check response was 20X
		return response.read()



	#
	# Utility methods
	#

	# build the URL to the given resource, using the hostname and userId
	def genResourceURL(self, resourceId):
		return self.hostname+"/users/"+str(self.userId)+"/resources/"+str(resourceId)

	# build the URL to the given stream, using the hostname, userId and resourceId
	def genStreamURL(self, resourceId, streamId):
		return self.genResourceURL(resourceId)+"/streams/"+str(streamId)

	# Ensure hostname and userId are set
	def valid(self):
		if self.userId==None:
			return False
		return True
	#
	# Getters and Setters
	#
	def setHostname(self,hostname):
		self.hostname = hostname

	def setUser(self,userId):
		self.userId = userId


