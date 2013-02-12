// @SOURCE:/Users/ljm/code/SicsthSense/cloud-altmanage/conf/routes
// @HASH:4b69019f771e0d5a8570c8a6d7be3e7d8a170852
// @DATE:Mon Feb 11 23:20:18 CET 2013

import Routes.{prefix => _prefix, defaultPrefix => _defaultPrefix}
import play.core._
import play.core.Router._
import play.core.j._

import play.api.mvc._
import play.libs.F

import Router.queryString


// @LINE:107
// @LINE:104
// @LINE:101
// @LINE:100
// @LINE:99
// @LINE:98
// @LINE:97
// @LINE:87
// @LINE:86
// @LINE:85
// @LINE:84
// @LINE:82
// @LINE:81
// @LINE:80
// @LINE:79
// @LINE:76
// @LINE:75
// @LINE:74
// @LINE:73
// @LINE:70
// @LINE:69
// @LINE:68
// @LINE:67
// @LINE:66
// @LINE:65
// @LINE:64
// @LINE:63
// @LINE:62
// @LINE:61
// @LINE:60
// @LINE:59
// @LINE:58
// @LINE:57
// @LINE:56
// @LINE:55
// @LINE:30
// @LINE:29
// @LINE:28
// @LINE:27
// @LINE:26
// @LINE:25
// @LINE:24
// @LINE:23
// @LINE:22
// @LINE:21
// @LINE:20
// @LINE:17
// @LINE:16
// @LINE:15
// @LINE:12
// @LINE:11
// @LINE:10
// @LINE:9
// @LINE:8
// @LINE:7
// @LINE:6
package controllers {

// @LINE:107
class ReverseAssets {
    

// @LINE:107
def at(file:String): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "assets/" + implicitly[PathBindable[String]].unbind("file", file))
}
                                                
    
}
                          

// @LINE:87
// @LINE:86
// @LINE:85
// @LINE:84
// @LINE:82
// @LINE:81
// @LINE:80
// @LINE:79
class ReverseProxy {
    

// @LINE:87
// @LINE:86
// @LINE:85
// @LINE:84
def forwardById(id:Long, arguments:String): Call = {
   (id: @unchecked, arguments: @unchecked) match {
// @LINE:84
case (id, arguments) if true => Call("GET", _prefix + { _defaultPrefix } + "proxy/" + implicitly[PathBindable[Long]].unbind("id", id) + queryString(List(Some(implicitly[QueryStringBindable[String]].unbind("arguments", arguments)))))
                                                        
// @LINE:85
case (id, arguments) if true => Call("POST", _prefix + { _defaultPrefix } + "proxy/" + implicitly[PathBindable[Long]].unbind("id", id) + queryString(List(Some(implicitly[QueryStringBindable[String]].unbind("arguments", arguments)))))
                                                        
// @LINE:86
case (id, arguments) if true => Call("PUT", _prefix + { _defaultPrefix } + "proxy/" + implicitly[PathBindable[Long]].unbind("id", id) + queryString(List(Some(implicitly[QueryStringBindable[String]].unbind("arguments", arguments)))))
                                                        
// @LINE:87
case (id, arguments) if true => Call("DELETE", _prefix + { _defaultPrefix } + "proxy/" + implicitly[PathBindable[Long]].unbind("id", id) + queryString(List(Some(implicitly[QueryStringBindable[String]].unbind("arguments", arguments)))))
                                                        
   }
}
                                                

// @LINE:82
// @LINE:81
// @LINE:80
// @LINE:79
def forwardByPath(user:String, endpoint:String, path:String): Call = {
   (user: @unchecked, endpoint: @unchecked, path: @unchecked) match {
// @LINE:79
case (user, endpoint, path) if true => Call("GET", _prefix + { _defaultPrefix } + "proxy/" + implicitly[PathBindable[String]].unbind("user", user) + "/" + implicitly[PathBindable[String]].unbind("endpoint", endpoint) + "/" + implicitly[PathBindable[String]].unbind("path", path))
                                                        
// @LINE:80
case (user, endpoint, path) if true => Call("POST", _prefix + { _defaultPrefix } + "proxy/" + implicitly[PathBindable[String]].unbind("user", user) + "/" + implicitly[PathBindable[String]].unbind("endpoint", endpoint) + "/" + implicitly[PathBindable[String]].unbind("path", path))
                                                        
// @LINE:81
case (user, endpoint, path) if true => Call("PUT", _prefix + { _defaultPrefix } + "proxy/" + implicitly[PathBindable[String]].unbind("user", user) + "/" + implicitly[PathBindable[String]].unbind("endpoint", endpoint) + "/" + implicitly[PathBindable[String]].unbind("path", path))
                                                        
// @LINE:82
case (user, endpoint, path) if true => Call("DELETE", _prefix + { _defaultPrefix } + "proxy/" + implicitly[PathBindable[String]].unbind("user", user) + "/" + implicitly[PathBindable[String]].unbind("endpoint", endpoint) + "/" + implicitly[PathBindable[String]].unbind("path", path))
                                                        
   }
}
                                                
    
}
                          

// @LINE:76
// @LINE:75
// @LINE:74
// @LINE:73
class ReverseStreams {
    

// @LINE:74
// @LINE:73
def getSecured(user:String, endpoint:String, path:String, tail:Long = -1, last:Long = -1, since:Long = -1): Call = {
   (user: @unchecked, endpoint: @unchecked, path: @unchecked, tail: @unchecked, last: @unchecked, since: @unchecked) match {
// @LINE:73
case (user, endpoint, path, tail, last, since) if true => Call("GET", _prefix + { _defaultPrefix } + "streams/" + implicitly[PathBindable[String]].unbind("user", user) + "/" + implicitly[PathBindable[String]].unbind("endpoint", endpoint) + "/" + implicitly[PathBindable[String]].unbind("path", path) + queryString(List(if(tail == -1) None else Some(implicitly[QueryStringBindable[Long]].unbind("tail", tail)), if(last == -1) None else Some(implicitly[QueryStringBindable[Long]].unbind("last", last)), if(since == -1) None else Some(implicitly[QueryStringBindable[Long]].unbind("since", since)))))
                                                        
// @LINE:74
case (user, endpoint, path, tail, last, since) if true => Call("GET", _prefix + { _defaultPrefix } + "streams/" + implicitly[PathBindable[String]].unbind("user", user) + "/" + implicitly[PathBindable[String]].unbind("endpoint", endpoint) + queryString(List(if(path == "") None else Some(implicitly[QueryStringBindable[String]].unbind("path", path)), if(tail == -1) None else Some(implicitly[QueryStringBindable[Long]].unbind("tail", tail)), if(last == -1) None else Some(implicitly[QueryStringBindable[Long]].unbind("last", last)), if(since == -1) None else Some(implicitly[QueryStringBindable[Long]].unbind("since", since)))))
                                                        
   }
}
                                                

// @LINE:76
// @LINE:75
def post(user:String, endpoint:String, path:String): Call = {
   (user: @unchecked, endpoint: @unchecked, path: @unchecked) match {
// @LINE:75
case (user, endpoint, path) if true => Call("POST", _prefix + { _defaultPrefix } + "streams/" + implicitly[PathBindable[String]].unbind("user", user) + "/" + implicitly[PathBindable[String]].unbind("endpoint", endpoint) + "/" + implicitly[PathBindable[String]].unbind("path", path))
                                                        
// @LINE:76
case (user, endpoint, path) if true => Call("POST", _prefix + { _defaultPrefix } + "streams/" + implicitly[PathBindable[String]].unbind("user", user) + "/" + implicitly[PathBindable[String]].unbind("endpoint", endpoint) + queryString(List(if(path == "") None else Some(implicitly[QueryStringBindable[String]].unbind("path", path)))))
                                                        
   }
}
                                                
    
}
                          

// @LINE:12
class ReversePublic {
    

// @LINE:12
def about(): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "API")
}
                                                
    
}
                          

// @LINE:70
// @LINE:69
// @LINE:68
// @LINE:67
// @LINE:66
// @LINE:65
// @LINE:64
// @LINE:63
// @LINE:62
// @LINE:61
// @LINE:60
// @LINE:59
// @LINE:58
// @LINE:57
// @LINE:56
// @LINE:55
class ReverseCtrlResource {
    

// @LINE:64
def toggleFollow(id:Long): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "resources/" + implicitly[PathBindable[Long]].unbind("id", id) + "/togglefollow")
}
                                                

// @LINE:59
def setLabelName(id:Long, label:String): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "resources/" + implicitly[PathBindable[Long]].unbind("id", id) + "/setLabel" + queryString(List(Some(implicitly[QueryStringBindable[String]].unbind("label", label)))))
}
                                                

// @LINE:69
def togglePublicAccess(id:Long): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "resources/" + implicitly[PathBindable[Long]].unbind("id", id) + "/togglePublicAccess")
}
                                                

// @LINE:57
// @LINE:56
def delete(id:Long): Call = {
   (id: @unchecked) match {
// @LINE:56
case (id) if true => Call("GET", _prefix + { _defaultPrefix } + "resources/" + implicitly[PathBindable[Long]].unbind("id", id) + "/delete")
                                                        
// @LINE:57
case (id) if true => Call("DELETE", _prefix + { _defaultPrefix } + "resources/" + implicitly[PathBindable[Long]].unbind("id", id))
                                                        
   }
}
                                                

// @LINE:63
def unfollow(id:Long): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "resources/" + implicitly[PathBindable[Long]].unbind("id", id) + "/unfollow")
}
                                                

// @LINE:55
def get(id:Long): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "resources/" + implicitly[PathBindable[Long]].unbind("id", id))
}
                                                

// @LINE:65
def isFollowing(id:Long): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "resources/" + implicitly[PathBindable[Long]].unbind("id", id) + "/isFollowing")
}
                                                

// @LINE:66
def setPublicAccess(id:Long): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "resources/" + implicitly[PathBindable[Long]].unbind("id", id) + "/setPublicAccess")
}
                                                

// @LINE:62
def follow(id:Long): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "resources/" + implicitly[PathBindable[Long]].unbind("id", id) + "/follow")
}
                                                

// @LINE:58
def setPeriod(id:Long, period:Long): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "resources/" + implicitly[PathBindable[Long]].unbind("id", id) + "/setPeriod" + queryString(List(Some(implicitly[QueryStringBindable[Long]].unbind("period", period)))))
}
                                                

// @LINE:60
def setInputParser(id:Long, parser:String): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "resources/" + implicitly[PathBindable[Long]].unbind("id", id) + "/setInputParser" + queryString(List(Some(implicitly[QueryStringBindable[String]].unbind("parser", parser)))))
}
                                                

// @LINE:70
def specifyPublicAccess(id:Long, access:Boolean): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "resources/" + implicitly[PathBindable[Long]].unbind("id", id) + "/specifyPublicAccess/" + implicitly[PathBindable[Boolean]].unbind("access", access))
}
                                                

// @LINE:68
def isPublicAccess(id:Long): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "resources/" + implicitly[PathBindable[Long]].unbind("id", id) + "/isPublicAccess")
}
                                                

// @LINE:61
def clearStream(id:Long): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "resources/" + implicitly[PathBindable[Long]].unbind("id", id) + "/clearStream")
}
                                                

// @LINE:67
def removePublicAccess(id:Long): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "resources/" + implicitly[PathBindable[Long]].unbind("id", id) + "/removePublicAccess")
}
                                                
    
}
                          

// @LINE:8
// @LINE:7
// @LINE:6
class ReverseLogin {
    

// @LINE:8
def logout(): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "logout")
}
                                                

// @LINE:6
def authenticate(openid_identifier:String): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "authenticate" + queryString(List(Some(implicitly[QueryStringBindable[String]].unbind("openid_identifier", openid_identifier)))))
}
                                                

// @LINE:7
def openIDCallback(): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "openIDCallback")
}
                                                
    
}
                          

// @LINE:104
// @LINE:11
// @LINE:10
// @LINE:9
class ReverseApplication {
    

// @LINE:10
def search(): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "search")
}
                                                

// @LINE:104
def javascriptRoutes(): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "assets/javascripts/routes")
}
                                                

// @LINE:11
def manage(): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "manage")
}
                                                

// @LINE:9
def home(): Call = {
   Call("GET", _prefix)
}
                                                
    
}
                          

// @LINE:101
// @LINE:100
// @LINE:99
// @LINE:98
// @LINE:97
class ReverseDummyThing {
    

// @LINE:99
def temperature(id:Long): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "dummy/" + implicitly[PathBindable[Long]].unbind("id", id) + "/sensors/temperature")
}
                                                

// @LINE:98
def sensors(id:Long): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "dummy/" + implicitly[PathBindable[Long]].unbind("id", id) + "/sensors")
}
                                                

// @LINE:101
def print(id:Long): Call = {
   Call("POST", _prefix + { _defaultPrefix } + "dummy/" + implicitly[PathBindable[Long]].unbind("id", id) + "/actuators/print")
}
                                                

// @LINE:97
def discover(id:Long): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "dummy/" + implicitly[PathBindable[Long]].unbind("id", id) + "/discover")
}
                                                

// @LINE:100
def energy(id:Long): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "dummy/" + implicitly[PathBindable[Long]].unbind("id", id) + "/sensors/energy")
}
                                                
    
}
                          

// @LINE:30
// @LINE:29
// @LINE:28
// @LINE:27
// @LINE:26
// @LINE:25
// @LINE:24
// @LINE:23
// @LINE:22
// @LINE:21
// @LINE:20
class ReverseCtrlEndPoint {
    

// @LINE:24
def submit(id:Long): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "endpoints/" + implicitly[PathBindable[Long]].unbind("id", id) + "/submit")
}
                                                

// @LINE:26
def addResource(id:Long): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "endpoints/" + implicitly[PathBindable[Long]].unbind("id", id) + "/addResource")
}
                                                

// @LINE:29
def toggleFollow(id:Long): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "endpoints/" + implicitly[PathBindable[Long]].unbind("id", id) + "/toggleFollow")
}
                                                

// @LINE:23
def delete(id:Long): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "endpoints/" + implicitly[PathBindable[Long]].unbind("id", id) + "/delete")
}
                                                

// @LINE:28
def unfollow(id:Long): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "endpoints/" + implicitly[PathBindable[Long]].unbind("id", id) + "/unfollow")
}
                                                

// @LINE:21
def get(id:Long): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "endpoints/" + implicitly[PathBindable[Long]].unbind("id", id))
}
                                                

// @LINE:30
def isFollowing(id:Long): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "endpoints/" + implicitly[PathBindable[Long]].unbind("id", id) + "/isFollowing")
}
                                                

// @LINE:25
def discover(id:Long): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "endpoints/" + implicitly[PathBindable[Long]].unbind("id", id) + "/discover")
}
                                                

// @LINE:27
def follow(id:Long): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "endpoints/" + implicitly[PathBindable[Long]].unbind("id", id) + "/follow")
}
                                                

// @LINE:22
def edit(id:Long): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "endpoints/" + implicitly[PathBindable[Long]].unbind("id", id) + "/edit")
}
                                                

// @LINE:20
def add(): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "endpoints/add")
}
                                                
    
}
                          

// @LINE:17
// @LINE:16
// @LINE:15
class ReverseCtrlUser {
    

// @LINE:17
def submit(): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "account/submit")
}
                                                

// @LINE:16
def edit(): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "account/edit")
}
                                                

// @LINE:15
def get(): Call = {
   Call("GET", _prefix + { _defaultPrefix } + "account")
}
                                                
    
}
                          
}
                  


// @LINE:107
// @LINE:104
// @LINE:101
// @LINE:100
// @LINE:99
// @LINE:98
// @LINE:97
// @LINE:87
// @LINE:86
// @LINE:85
// @LINE:84
// @LINE:82
// @LINE:81
// @LINE:80
// @LINE:79
// @LINE:76
// @LINE:75
// @LINE:74
// @LINE:73
// @LINE:70
// @LINE:69
// @LINE:68
// @LINE:67
// @LINE:66
// @LINE:65
// @LINE:64
// @LINE:63
// @LINE:62
// @LINE:61
// @LINE:60
// @LINE:59
// @LINE:58
// @LINE:57
// @LINE:56
// @LINE:55
// @LINE:30
// @LINE:29
// @LINE:28
// @LINE:27
// @LINE:26
// @LINE:25
// @LINE:24
// @LINE:23
// @LINE:22
// @LINE:21
// @LINE:20
// @LINE:17
// @LINE:16
// @LINE:15
// @LINE:12
// @LINE:11
// @LINE:10
// @LINE:9
// @LINE:8
// @LINE:7
// @LINE:6
package controllers.javascript {

// @LINE:107
class ReverseAssets {
    

// @LINE:107
def at : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.Assets.at",
   """
      function(file) {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "assets/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("file", file)})
      }
   """
)
                        
    
}
              

// @LINE:87
// @LINE:86
// @LINE:85
// @LINE:84
// @LINE:82
// @LINE:81
// @LINE:80
// @LINE:79
class ReverseProxy {
    

// @LINE:87
// @LINE:86
// @LINE:85
// @LINE:84
def forwardById : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.Proxy.forwardById",
   """
      function(id, arguments) {
      if (true) {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "proxy/" + (""" + implicitly[PathBindable[Long]].javascriptUnbind + """)("id", id) + _qS([(""" + implicitly[QueryStringBindable[String]].javascriptUnbind + """)("arguments", arguments)])})
      }
      if (true) {
      return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "proxy/" + (""" + implicitly[PathBindable[Long]].javascriptUnbind + """)("id", id) + _qS([(""" + implicitly[QueryStringBindable[String]].javascriptUnbind + """)("arguments", arguments)])})
      }
      if (true) {
      return _wA({method:"PUT", url:"""" + _prefix + { _defaultPrefix } + """" + "proxy/" + (""" + implicitly[PathBindable[Long]].javascriptUnbind + """)("id", id) + _qS([(""" + implicitly[QueryStringBindable[String]].javascriptUnbind + """)("arguments", arguments)])})
      }
      if (true) {
      return _wA({method:"DELETE", url:"""" + _prefix + { _defaultPrefix } + """" + "proxy/" + (""" + implicitly[PathBindable[Long]].javascriptUnbind + """)("id", id) + _qS([(""" + implicitly[QueryStringBindable[String]].javascriptUnbind + """)("arguments", arguments)])})
      }
      }
   """
)
                        

// @LINE:82
// @LINE:81
// @LINE:80
// @LINE:79
def forwardByPath : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.Proxy.forwardByPath",
   """
      function(user, endpoint, path) {
      if (true) {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "proxy/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("user", user) + "/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("endpoint", endpoint) + "/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("path", path)})
      }
      if (true) {
      return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "proxy/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("user", user) + "/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("endpoint", endpoint) + "/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("path", path)})
      }
      if (true) {
      return _wA({method:"PUT", url:"""" + _prefix + { _defaultPrefix } + """" + "proxy/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("user", user) + "/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("endpoint", endpoint) + "/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("path", path)})
      }
      if (true) {
      return _wA({method:"DELETE", url:"""" + _prefix + { _defaultPrefix } + """" + "proxy/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("user", user) + "/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("endpoint", endpoint) + "/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("path", path)})
      }
      }
   """
)
                        
    
}
              

// @LINE:76
// @LINE:75
// @LINE:74
// @LINE:73
class ReverseStreams {
    

// @LINE:74
// @LINE:73
def getSecured : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.Streams.getSecured",
   """
      function(user, endpoint, path, tail, last, since) {
      if (true) {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "streams/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("user", user) + "/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("endpoint", endpoint) + "/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("path", path) + _qS([(tail == null ? """ +  implicitly[JavascriptLitteral[Long]].to(-1) + """ : (""" + implicitly[QueryStringBindable[Long]].javascriptUnbind + """)("tail", tail)), (last == null ? """ +  implicitly[JavascriptLitteral[Long]].to(-1) + """ : (""" + implicitly[QueryStringBindable[Long]].javascriptUnbind + """)("last", last)), (since == null ? """ +  implicitly[JavascriptLitteral[Long]].to(-1) + """ : (""" + implicitly[QueryStringBindable[Long]].javascriptUnbind + """)("since", since))])})
      }
      if (true) {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "streams/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("user", user) + "/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("endpoint", endpoint) + _qS([(path == null ? """ +  implicitly[JavascriptLitteral[String]].to("") + """ : (""" + implicitly[QueryStringBindable[String]].javascriptUnbind + """)("path", path)), (tail == null ? """ +  implicitly[JavascriptLitteral[Long]].to(-1) + """ : (""" + implicitly[QueryStringBindable[Long]].javascriptUnbind + """)("tail", tail)), (last == null ? """ +  implicitly[JavascriptLitteral[Long]].to(-1) + """ : (""" + implicitly[QueryStringBindable[Long]].javascriptUnbind + """)("last", last)), (since == null ? """ +  implicitly[JavascriptLitteral[Long]].to(-1) + """ : (""" + implicitly[QueryStringBindable[Long]].javascriptUnbind + """)("since", since))])})
      }
      }
   """
)
                        

// @LINE:76
// @LINE:75
def post : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.Streams.post",
   """
      function(user, endpoint, path) {
      if (true) {
      return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "streams/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("user", user) + "/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("endpoint", endpoint) + "/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("path", path)})
      }
      if (true) {
      return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "streams/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("user", user) + "/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("endpoint", endpoint) + _qS([(path == null ? """ +  implicitly[JavascriptLitteral[String]].to("") + """ : (""" + implicitly[QueryStringBindable[String]].javascriptUnbind + """)("path", path))])})
      }
      }
   """
)
                        
    
}
              

// @LINE:12
class ReversePublic {
    

// @LINE:12
def about : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.Public.about",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "API"})
      }
   """
)
                        
    
}
              

// @LINE:70
// @LINE:69
// @LINE:68
// @LINE:67
// @LINE:66
// @LINE:65
// @LINE:64
// @LINE:63
// @LINE:62
// @LINE:61
// @LINE:60
// @LINE:59
// @LINE:58
// @LINE:57
// @LINE:56
// @LINE:55
class ReverseCtrlResource {
    

// @LINE:64
def toggleFollow : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.CtrlResource.toggleFollow",
   """
      function(id) {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "resources/" + (""" + implicitly[PathBindable[Long]].javascriptUnbind + """)("id", id) + "/togglefollow"})
      }
   """
)
                        

// @LINE:59
def setLabelName : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.CtrlResource.setLabelName",
   """
      function(id,label) {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "resources/" + (""" + implicitly[PathBindable[Long]].javascriptUnbind + """)("id", id) + "/setLabel" + _qS([(""" + implicitly[QueryStringBindable[String]].javascriptUnbind + """)("label", label)])})
      }
   """
)
                        

// @LINE:69
def togglePublicAccess : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.CtrlResource.togglePublicAccess",
   """
      function(id) {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "resources/" + (""" + implicitly[PathBindable[Long]].javascriptUnbind + """)("id", id) + "/togglePublicAccess"})
      }
   """
)
                        

// @LINE:57
// @LINE:56
def delete : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.CtrlResource.delete",
   """
      function(id) {
      if (true) {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "resources/" + (""" + implicitly[PathBindable[Long]].javascriptUnbind + """)("id", id) + "/delete"})
      }
      if (true) {
      return _wA({method:"DELETE", url:"""" + _prefix + { _defaultPrefix } + """" + "resources/" + (""" + implicitly[PathBindable[Long]].javascriptUnbind + """)("id", id)})
      }
      }
   """
)
                        

// @LINE:63
def unfollow : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.CtrlResource.unfollow",
   """
      function(id) {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "resources/" + (""" + implicitly[PathBindable[Long]].javascriptUnbind + """)("id", id) + "/unfollow"})
      }
   """
)
                        

// @LINE:55
def get : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.CtrlResource.get",
   """
      function(id) {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "resources/" + (""" + implicitly[PathBindable[Long]].javascriptUnbind + """)("id", id)})
      }
   """
)
                        

// @LINE:65
def isFollowing : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.CtrlResource.isFollowing",
   """
      function(id) {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "resources/" + (""" + implicitly[PathBindable[Long]].javascriptUnbind + """)("id", id) + "/isFollowing"})
      }
   """
)
                        

// @LINE:66
def setPublicAccess : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.CtrlResource.setPublicAccess",
   """
      function(id) {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "resources/" + (""" + implicitly[PathBindable[Long]].javascriptUnbind + """)("id", id) + "/setPublicAccess"})
      }
   """
)
                        

// @LINE:62
def follow : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.CtrlResource.follow",
   """
      function(id) {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "resources/" + (""" + implicitly[PathBindable[Long]].javascriptUnbind + """)("id", id) + "/follow"})
      }
   """
)
                        

// @LINE:58
def setPeriod : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.CtrlResource.setPeriod",
   """
      function(id,period) {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "resources/" + (""" + implicitly[PathBindable[Long]].javascriptUnbind + """)("id", id) + "/setPeriod" + _qS([(""" + implicitly[QueryStringBindable[Long]].javascriptUnbind + """)("period", period)])})
      }
   """
)
                        

// @LINE:60
def setInputParser : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.CtrlResource.setInputParser",
   """
      function(id,parser) {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "resources/" + (""" + implicitly[PathBindable[Long]].javascriptUnbind + """)("id", id) + "/setInputParser" + _qS([(""" + implicitly[QueryStringBindable[String]].javascriptUnbind + """)("parser", parser)])})
      }
   """
)
                        

// @LINE:70
def specifyPublicAccess : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.CtrlResource.specifyPublicAccess",
   """
      function(id,access) {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "resources/" + (""" + implicitly[PathBindable[Long]].javascriptUnbind + """)("id", id) + "/specifyPublicAccess/" + (""" + implicitly[PathBindable[Boolean]].javascriptUnbind + """)("access", access)})
      }
   """
)
                        

// @LINE:68
def isPublicAccess : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.CtrlResource.isPublicAccess",
   """
      function(id) {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "resources/" + (""" + implicitly[PathBindable[Long]].javascriptUnbind + """)("id", id) + "/isPublicAccess"})
      }
   """
)
                        

// @LINE:61
def clearStream : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.CtrlResource.clearStream",
   """
      function(id) {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "resources/" + (""" + implicitly[PathBindable[Long]].javascriptUnbind + """)("id", id) + "/clearStream"})
      }
   """
)
                        

// @LINE:67
def removePublicAccess : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.CtrlResource.removePublicAccess",
   """
      function(id) {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "resources/" + (""" + implicitly[PathBindable[Long]].javascriptUnbind + """)("id", id) + "/removePublicAccess"})
      }
   """
)
                        
    
}
              

// @LINE:8
// @LINE:7
// @LINE:6
class ReverseLogin {
    

// @LINE:8
def logout : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.Login.logout",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "logout"})
      }
   """
)
                        

// @LINE:6
def authenticate : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.Login.authenticate",
   """
      function(openid_identifier) {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "authenticate" + _qS([(""" + implicitly[QueryStringBindable[String]].javascriptUnbind + """)("openid_identifier", openid_identifier)])})
      }
   """
)
                        

// @LINE:7
def openIDCallback : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.Login.openIDCallback",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "openIDCallback"})
      }
   """
)
                        
    
}
              

// @LINE:104
// @LINE:11
// @LINE:10
// @LINE:9
class ReverseApplication {
    

// @LINE:10
def search : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.Application.search",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "search"})
      }
   """
)
                        

// @LINE:104
def javascriptRoutes : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.Application.javascriptRoutes",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "assets/javascripts/routes"})
      }
   """
)
                        

// @LINE:11
def manage : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.Application.manage",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "manage"})
      }
   """
)
                        

// @LINE:9
def home : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.Application.home",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + """"})
      }
   """
)
                        
    
}
              

// @LINE:101
// @LINE:100
// @LINE:99
// @LINE:98
// @LINE:97
class ReverseDummyThing {
    

// @LINE:99
def temperature : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.DummyThing.temperature",
   """
      function(id) {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "dummy/" + (""" + implicitly[PathBindable[Long]].javascriptUnbind + """)("id", id) + "/sensors/temperature"})
      }
   """
)
                        

// @LINE:98
def sensors : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.DummyThing.sensors",
   """
      function(id) {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "dummy/" + (""" + implicitly[PathBindable[Long]].javascriptUnbind + """)("id", id) + "/sensors"})
      }
   """
)
                        

// @LINE:101
def print : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.DummyThing.print",
   """
      function(id) {
      return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "dummy/" + (""" + implicitly[PathBindable[Long]].javascriptUnbind + """)("id", id) + "/actuators/print"})
      }
   """
)
                        

// @LINE:97
def discover : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.DummyThing.discover",
   """
      function(id) {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "dummy/" + (""" + implicitly[PathBindable[Long]].javascriptUnbind + """)("id", id) + "/discover"})
      }
   """
)
                        

// @LINE:100
def energy : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.DummyThing.energy",
   """
      function(id) {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "dummy/" + (""" + implicitly[PathBindable[Long]].javascriptUnbind + """)("id", id) + "/sensors/energy"})
      }
   """
)
                        
    
}
              

// @LINE:30
// @LINE:29
// @LINE:28
// @LINE:27
// @LINE:26
// @LINE:25
// @LINE:24
// @LINE:23
// @LINE:22
// @LINE:21
// @LINE:20
class ReverseCtrlEndPoint {
    

// @LINE:24
def submit : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.CtrlEndPoint.submit",
   """
      function(id) {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "endpoints/" + (""" + implicitly[PathBindable[Long]].javascriptUnbind + """)("id", id) + "/submit"})
      }
   """
)
                        

// @LINE:26
def addResource : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.CtrlEndPoint.addResource",
   """
      function(id) {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "endpoints/" + (""" + implicitly[PathBindable[Long]].javascriptUnbind + """)("id", id) + "/addResource"})
      }
   """
)
                        

// @LINE:29
def toggleFollow : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.CtrlEndPoint.toggleFollow",
   """
      function(id) {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "endpoints/" + (""" + implicitly[PathBindable[Long]].javascriptUnbind + """)("id", id) + "/toggleFollow"})
      }
   """
)
                        

// @LINE:23
def delete : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.CtrlEndPoint.delete",
   """
      function(id) {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "endpoints/" + (""" + implicitly[PathBindable[Long]].javascriptUnbind + """)("id", id) + "/delete"})
      }
   """
)
                        

// @LINE:28
def unfollow : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.CtrlEndPoint.unfollow",
   """
      function(id) {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "endpoints/" + (""" + implicitly[PathBindable[Long]].javascriptUnbind + """)("id", id) + "/unfollow"})
      }
   """
)
                        

// @LINE:21
def get : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.CtrlEndPoint.get",
   """
      function(id) {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "endpoints/" + (""" + implicitly[PathBindable[Long]].javascriptUnbind + """)("id", id)})
      }
   """
)
                        

// @LINE:30
def isFollowing : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.CtrlEndPoint.isFollowing",
   """
      function(id) {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "endpoints/" + (""" + implicitly[PathBindable[Long]].javascriptUnbind + """)("id", id) + "/isFollowing"})
      }
   """
)
                        

// @LINE:25
def discover : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.CtrlEndPoint.discover",
   """
      function(id) {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "endpoints/" + (""" + implicitly[PathBindable[Long]].javascriptUnbind + """)("id", id) + "/discover"})
      }
   """
)
                        

// @LINE:27
def follow : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.CtrlEndPoint.follow",
   """
      function(id) {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "endpoints/" + (""" + implicitly[PathBindable[Long]].javascriptUnbind + """)("id", id) + "/follow"})
      }
   """
)
                        

// @LINE:22
def edit : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.CtrlEndPoint.edit",
   """
      function(id) {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "endpoints/" + (""" + implicitly[PathBindable[Long]].javascriptUnbind + """)("id", id) + "/edit"})
      }
   """
)
                        

// @LINE:20
def add : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.CtrlEndPoint.add",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "endpoints/add"})
      }
   """
)
                        
    
}
              

// @LINE:17
// @LINE:16
// @LINE:15
class ReverseCtrlUser {
    

// @LINE:17
def submit : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.CtrlUser.submit",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "account/submit"})
      }
   """
)
                        

// @LINE:16
def edit : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.CtrlUser.edit",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "account/edit"})
      }
   """
)
                        

// @LINE:15
def get : JavascriptReverseRoute = JavascriptReverseRoute(
   "controllers.CtrlUser.get",
   """
      function() {
      return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "account"})
      }
   """
)
                        
    
}
              
}
        


// @LINE:107
// @LINE:104
// @LINE:101
// @LINE:100
// @LINE:99
// @LINE:98
// @LINE:97
// @LINE:87
// @LINE:86
// @LINE:85
// @LINE:84
// @LINE:82
// @LINE:81
// @LINE:80
// @LINE:79
// @LINE:76
// @LINE:75
// @LINE:74
// @LINE:73
// @LINE:70
// @LINE:69
// @LINE:68
// @LINE:67
// @LINE:66
// @LINE:65
// @LINE:64
// @LINE:63
// @LINE:62
// @LINE:61
// @LINE:60
// @LINE:59
// @LINE:58
// @LINE:57
// @LINE:56
// @LINE:55
// @LINE:30
// @LINE:29
// @LINE:28
// @LINE:27
// @LINE:26
// @LINE:25
// @LINE:24
// @LINE:23
// @LINE:22
// @LINE:21
// @LINE:20
// @LINE:17
// @LINE:16
// @LINE:15
// @LINE:12
// @LINE:11
// @LINE:10
// @LINE:9
// @LINE:8
// @LINE:7
// @LINE:6
package controllers.ref {

// @LINE:107
class ReverseAssets {
    

// @LINE:107
def at(path:String, file:String): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.Assets.at(path, file), HandlerDef(this, "controllers.Assets", "at", Seq(classOf[String], classOf[String]), "GET", """ Map static resources from the /public folder to the /assets URL path""", _prefix + """assets/$file<.+>""")
)
                      
    
}
                          

// @LINE:87
// @LINE:86
// @LINE:85
// @LINE:84
// @LINE:82
// @LINE:81
// @LINE:80
// @LINE:79
class ReverseProxy {
    

// @LINE:84
def forwardById(id:Long, arguments:String): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.Proxy.forwardById(id, arguments), HandlerDef(this, "controllers.Proxy", "forwardById", Seq(classOf[Long], classOf[String]), "GET", """""", _prefix + """proxy/$id<[^/]+>""")
)
                      

// @LINE:79
def forwardByPath(user:String, endpoint:String, path:String): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.Proxy.forwardByPath(user, endpoint, path), HandlerDef(this, "controllers.Proxy", "forwardByPath", Seq(classOf[String], classOf[String], classOf[String]), "GET", """ Proxy""", _prefix + """proxy/$user<[^/]+>/$endpoint<[^/]+>/$path<.*>""")
)
                      
    
}
                          

// @LINE:76
// @LINE:75
// @LINE:74
// @LINE:73
class ReverseStreams {
    

// @LINE:73
def getSecured(user:String, endpoint:String, path:String, tail:Long, last:Long, since:Long): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.Streams.getSecured(user, endpoint, path, tail, last, since), HandlerDef(this, "controllers.Streams", "getSecured", Seq(classOf[String], classOf[String], classOf[String], classOf[Long], classOf[Long], classOf[Long]), "GET", """ Streams""", _prefix + """streams/$user<[^/]+>/$endpoint<[^/]+>/$path<.*>""")
)
                      

// @LINE:75
def post(user:String, endpoint:String, path:String): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.Streams.post(user, endpoint, path), HandlerDef(this, "controllers.Streams", "post", Seq(classOf[String], classOf[String], classOf[String]), "POST", """""", _prefix + """streams/$user<[^/]+>/$endpoint<[^/]+>/$path<.*>""")
)
                      
    
}
                          

// @LINE:12
class ReversePublic {
    

// @LINE:12
def about(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.Public.about(), HandlerDef(this, "controllers.Public", "about", Seq(), "GET", """""", _prefix + """API""")
)
                      
    
}
                          

// @LINE:70
// @LINE:69
// @LINE:68
// @LINE:67
// @LINE:66
// @LINE:65
// @LINE:64
// @LINE:63
// @LINE:62
// @LINE:61
// @LINE:60
// @LINE:59
// @LINE:58
// @LINE:57
// @LINE:56
// @LINE:55
class ReverseCtrlResource {
    

// @LINE:64
def toggleFollow(id:Long): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.CtrlResource.toggleFollow(id), HandlerDef(this, "controllers.CtrlResource", "toggleFollow", Seq(classOf[Long]), "GET", """""", _prefix + """resources/$id<[^/]+>/togglefollow""")
)
                      

// @LINE:59
def setLabelName(id:Long, label:String): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.CtrlResource.setLabelName(id, label), HandlerDef(this, "controllers.CtrlResource", "setLabelName", Seq(classOf[Long], classOf[String]), "GET", """""", _prefix + """resources/$id<[^/]+>/setLabel""")
)
                      

// @LINE:69
def togglePublicAccess(id:Long): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.CtrlResource.togglePublicAccess(id), HandlerDef(this, "controllers.CtrlResource", "togglePublicAccess", Seq(classOf[Long]), "GET", """""", _prefix + """resources/$id<[^/]+>/togglePublicAccess""")
)
                      

// @LINE:56
def delete(id:Long): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.CtrlResource.delete(id), HandlerDef(this, "controllers.CtrlResource", "delete", Seq(classOf[Long]), "GET", """""", _prefix + """resources/$id<[^/]+>/delete""")
)
                      

// @LINE:63
def unfollow(id:Long): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.CtrlResource.unfollow(id), HandlerDef(this, "controllers.CtrlResource", "unfollow", Seq(classOf[Long]), "GET", """""", _prefix + """resources/$id<[^/]+>/unfollow""")
)
                      

// @LINE:55
def get(id:Long): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.CtrlResource.get(id), HandlerDef(this, "controllers.CtrlResource", "get", Seq(classOf[Long]), "GET", """ Resources
 with ID shown in the url""", _prefix + """resources/$id<[^/]+>""")
)
                      

// @LINE:65
def isFollowing(id:Long): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.CtrlResource.isFollowing(id), HandlerDef(this, "controllers.CtrlResource", "isFollowing", Seq(classOf[Long]), "GET", """""", _prefix + """resources/$id<[^/]+>/isFollowing""")
)
                      

// @LINE:66
def setPublicAccess(id:Long): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.CtrlResource.setPublicAccess(id), HandlerDef(this, "controllers.CtrlResource", "setPublicAccess", Seq(classOf[Long]), "GET", """""", _prefix + """resources/$id<[^/]+>/setPublicAccess""")
)
                      

// @LINE:62
def follow(id:Long): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.CtrlResource.follow(id), HandlerDef(this, "controllers.CtrlResource", "follow", Seq(classOf[Long]), "GET", """""", _prefix + """resources/$id<[^/]+>/follow""")
)
                      

// @LINE:58
def setPeriod(id:Long, period:Long): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.CtrlResource.setPeriod(id, period), HandlerDef(this, "controllers.CtrlResource", "setPeriod", Seq(classOf[Long], classOf[Long]), "GET", """""", _prefix + """resources/$id<[^/]+>/setPeriod""")
)
                      

// @LINE:60
def setInputParser(id:Long, parser:String): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.CtrlResource.setInputParser(id, parser), HandlerDef(this, "controllers.CtrlResource", "setInputParser", Seq(classOf[Long], classOf[String]), "GET", """""", _prefix + """resources/$id<[^/]+>/setInputParser""")
)
                      

// @LINE:70
def specifyPublicAccess(id:Long, access:Boolean): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.CtrlResource.specifyPublicAccess(id, access), HandlerDef(this, "controllers.CtrlResource", "specifyPublicAccess", Seq(classOf[Long], classOf[Boolean]), "GET", """""", _prefix + """resources/$id<[^/]+>/specifyPublicAccess/$access<[^/]+>""")
)
                      

// @LINE:68
def isPublicAccess(id:Long): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.CtrlResource.isPublicAccess(id), HandlerDef(this, "controllers.CtrlResource", "isPublicAccess", Seq(classOf[Long]), "GET", """""", _prefix + """resources/$id<[^/]+>/isPublicAccess""")
)
                      

// @LINE:61
def clearStream(id:Long): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.CtrlResource.clearStream(id), HandlerDef(this, "controllers.CtrlResource", "clearStream", Seq(classOf[Long]), "GET", """""", _prefix + """resources/$id<[^/]+>/clearStream""")
)
                      

// @LINE:67
def removePublicAccess(id:Long): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.CtrlResource.removePublicAccess(id), HandlerDef(this, "controllers.CtrlResource", "removePublicAccess", Seq(classOf[Long]), "GET", """""", _prefix + """resources/$id<[^/]+>/removePublicAccess""")
)
                      
    
}
                          

// @LINE:8
// @LINE:7
// @LINE:6
class ReverseLogin {
    

// @LINE:8
def logout(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.Login.logout(), HandlerDef(this, "controllers.Login", "logout", Seq(), "GET", """""", _prefix + """logout""")
)
                      

// @LINE:6
def authenticate(openid_identifier:String): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.Login.authenticate(openid_identifier), HandlerDef(this, "controllers.Login", "authenticate", Seq(classOf[String]), "GET", """ Pages""", _prefix + """authenticate""")
)
                      

// @LINE:7
def openIDCallback(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.Login.openIDCallback(), HandlerDef(this, "controllers.Login", "openIDCallback", Seq(), "GET", """""", _prefix + """openIDCallback""")
)
                      
    
}
                          

// @LINE:104
// @LINE:11
// @LINE:10
// @LINE:9
class ReverseApplication {
    

// @LINE:10
def search(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.Application.search(), HandlerDef(this, "controllers.Application", "search", Seq(), "GET", """""", _prefix + """search""")
)
                      

// @LINE:104
def javascriptRoutes(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.Application.javascriptRoutes(), HandlerDef(this, "controllers.Application", "javascriptRoutes", Seq(), "GET", """ Javascript Routes""", _prefix + """assets/javascripts/routes""")
)
                      

// @LINE:11
def manage(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.Application.manage(), HandlerDef(this, "controllers.Application", "manage", Seq(), "GET", """""", _prefix + """manage""")
)
                      

// @LINE:9
def home(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.Application.home(), HandlerDef(this, "controllers.Application", "home", Seq(), "GET", """""", _prefix + """""")
)
                      
    
}
                          

// @LINE:101
// @LINE:100
// @LINE:99
// @LINE:98
// @LINE:97
class ReverseDummyThing {
    

// @LINE:99
def temperature(id:Long): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.DummyThing.temperature(id), HandlerDef(this, "controllers.DummyThing", "temperature", Seq(classOf[Long]), "GET", """""", _prefix + """dummy/$id<[^/]+>/sensors/temperature""")
)
                      

// @LINE:98
def sensors(id:Long): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.DummyThing.sensors(id), HandlerDef(this, "controllers.DummyThing", "sensors", Seq(classOf[Long]), "GET", """""", _prefix + """dummy/$id<[^/]+>/sensors""")
)
                      

// @LINE:101
def print(id:Long): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.DummyThing.print(id), HandlerDef(this, "controllers.DummyThing", "print", Seq(classOf[Long]), "POST", """""", _prefix + """dummy/$id<[^/]+>/actuators/print""")
)
                      

// @LINE:97
def discover(id:Long): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.DummyThing.discover(id), HandlerDef(this, "controllers.DummyThing", "discover", Seq(classOf[Long]), "GET", """ Dummy thing""", _prefix + """dummy/$id<[^/]+>/discover""")
)
                      

// @LINE:100
def energy(id:Long): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.DummyThing.energy(id), HandlerDef(this, "controllers.DummyThing", "energy", Seq(classOf[Long]), "GET", """""", _prefix + """dummy/$id<[^/]+>/sensors/energy""")
)
                      
    
}
                          

// @LINE:30
// @LINE:29
// @LINE:28
// @LINE:27
// @LINE:26
// @LINE:25
// @LINE:24
// @LINE:23
// @LINE:22
// @LINE:21
// @LINE:20
class ReverseCtrlEndPoint {
    

// @LINE:24
def submit(id:Long): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.CtrlEndPoint.submit(id), HandlerDef(this, "controllers.CtrlEndPoint", "submit", Seq(classOf[Long]), "GET", """""", _prefix + """endpoints/$id<[^/]+>/submit""")
)
                      

// @LINE:26
def addResource(id:Long): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.CtrlEndPoint.addResource(id), HandlerDef(this, "controllers.CtrlEndPoint", "addResource", Seq(classOf[Long]), "GET", """""", _prefix + """endpoints/$id<[^/]+>/addResource""")
)
                      

// @LINE:29
def toggleFollow(id:Long): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.CtrlEndPoint.toggleFollow(id), HandlerDef(this, "controllers.CtrlEndPoint", "toggleFollow", Seq(classOf[Long]), "GET", """""", _prefix + """endpoints/$id<[^/]+>/toggleFollow""")
)
                      

// @LINE:23
def delete(id:Long): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.CtrlEndPoint.delete(id), HandlerDef(this, "controllers.CtrlEndPoint", "delete", Seq(classOf[Long]), "GET", """""", _prefix + """endpoints/$id<[^/]+>/delete""")
)
                      

// @LINE:28
def unfollow(id:Long): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.CtrlEndPoint.unfollow(id), HandlerDef(this, "controllers.CtrlEndPoint", "unfollow", Seq(classOf[Long]), "GET", """""", _prefix + """endpoints/$id<[^/]+>/unfollow""")
)
                      

// @LINE:21
def get(id:Long): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.CtrlEndPoint.get(id), HandlerDef(this, "controllers.CtrlEndPoint", "get", Seq(classOf[Long]), "GET", """""", _prefix + """endpoints/$id<[^/]+>""")
)
                      

// @LINE:30
def isFollowing(id:Long): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.CtrlEndPoint.isFollowing(id), HandlerDef(this, "controllers.CtrlEndPoint", "isFollowing", Seq(classOf[Long]), "GET", """""", _prefix + """endpoints/$id<[^/]+>/isFollowing""")
)
                      

// @LINE:25
def discover(id:Long): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.CtrlEndPoint.discover(id), HandlerDef(this, "controllers.CtrlEndPoint", "discover", Seq(classOf[Long]), "GET", """""", _prefix + """endpoints/$id<[^/]+>/discover""")
)
                      

// @LINE:27
def follow(id:Long): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.CtrlEndPoint.follow(id), HandlerDef(this, "controllers.CtrlEndPoint", "follow", Seq(classOf[Long]), "GET", """""", _prefix + """endpoints/$id<[^/]+>/follow""")
)
                      

// @LINE:22
def edit(id:Long): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.CtrlEndPoint.edit(id), HandlerDef(this, "controllers.CtrlEndPoint", "edit", Seq(classOf[Long]), "GET", """""", _prefix + """endpoints/$id<[^/]+>/edit""")
)
                      

// @LINE:20
def add(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.CtrlEndPoint.add(), HandlerDef(this, "controllers.CtrlEndPoint", "add", Seq(), "GET", """ End points""", _prefix + """endpoints/add""")
)
                      
    
}
                          

// @LINE:17
// @LINE:16
// @LINE:15
class ReverseCtrlUser {
    

// @LINE:17
def submit(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.CtrlUser.submit(), HandlerDef(this, "controllers.CtrlUser", "submit", Seq(), "GET", """""", _prefix + """account/submit""")
)
                      

// @LINE:16
def edit(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.CtrlUser.edit(), HandlerDef(this, "controllers.CtrlUser", "edit", Seq(), "GET", """""", _prefix + """account/edit""")
)
                      

// @LINE:15
def get(): play.api.mvc.HandlerRef[_] = new play.api.mvc.HandlerRef(
   controllers.CtrlUser.get(), HandlerDef(this, "controllers.CtrlUser", "get", Seq(), "GET", """ Accounts""", _prefix + """account""")
)
                      
    
}
                          
}
                  
      