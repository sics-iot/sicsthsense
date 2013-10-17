#!/usr/bin/python
import urllib, urllib2
import simplejson as json

version = 0.1

class Engine:
	"""Object for interacting with the SicsthSense Engine"""
	#hostname = "http://sense.sics.se:8080" 
	hostname = "http://localhost:8080" 

	def __init__(self, userId=None):
		self.setUser(userId)

	def registerUser(userJSON):
		pass
	def updateUser(userId,userJSON):
		pass
	def deleteUser(userId):
		pass

	# Resource CRUD
	def createResource(resourceJSON):
		pass
	def updateResource(resourceId,resourceJSON):
		pass
	def deleteResource(resourceId):
		pass

	# Stream CRUD
	def createStream(resourceId,streamJSON):
		pass
	def updateStream(streamId,streamJSON):
		pass
	def deleteStream(streamId):
		pass

	# Data posting 
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
			print response.info()
			return None
		# check response code
		if response.getcode() > 400:
			print "Error: HTTP return code "+response.getcode()
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
		# check response code
		if response.getcode() > 400:
			print "Error: HTTP return code "+response.getcode()
			print response.info()
		return response.read()


	# Data getting

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

	# Build the URL to the given resource, using the hostname and userId
	def genResourceURL(self, resourceId):
		return self.hostname+"/users/"+str(self.userId)+"/resources/"+str(resourceId)

	# Build the URL to the given stream, using the hostname, userId and resourceId
	def genStreamURL(self, resourceId, streamId):
		return self.genResourceURL(resourceId)+"/streams/"+str(streamId)

	# Ensure hostname and userId are set
	def valid(self):
		if self.userId==None:
			return False
		return True

	# Do client side verification of JSON to send
	def checkJSON(data):
		return True


	#
	# Getters and Setters
	#
	def setHostname(self,hostname):
		self.hostname = hostname

	def setUser(self,userId):
		self.userId = userId


