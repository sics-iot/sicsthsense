// @SOURCE:/Users/ljm/code/SicsthSense/cloud-altmanage/conf/routes
// @HASH:4b69019f771e0d5a8570c8a6d7be3e7d8a170852
// @DATE:Mon Feb 11 23:20:18 CET 2013


import play.core._
import play.core.Router._
import play.core.j._

import play.api.mvc._
import play.libs.F

import Router.queryString

object Routes extends Router.Routes {

private var _prefix = "/"

def setPrefix(prefix: String) {
  _prefix = prefix  
  List[(String,Routes)]().foreach {
    case (p, router) => router.setPrefix(prefix + (if(prefix.endsWith("/")) "" else "/") + p)
  }
}

def prefix = _prefix

lazy val defaultPrefix = { if(Routes.prefix.endsWith("/")) "" else "/" } 


// @LINE:6
private[this] lazy val controllers_Login_authenticate0 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("authenticate"))))
        

// @LINE:7
private[this] lazy val controllers_Login_openIDCallback1 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("openIDCallback"))))
        

// @LINE:8
private[this] lazy val controllers_Login_logout2 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("logout"))))
        

// @LINE:9
private[this] lazy val controllers_Application_home3 = Route("GET", PathPattern(List(StaticPart(Routes.prefix))))
        

// @LINE:10
private[this] lazy val controllers_Application_search4 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("search"))))
        

// @LINE:11
private[this] lazy val controllers_Application_manage5 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("manage"))))
        

// @LINE:12
private[this] lazy val controllers_Public_about6 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("API"))))
        

// @LINE:15
private[this] lazy val controllers_CtrlUser_get7 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("account"))))
        

// @LINE:16
private[this] lazy val controllers_CtrlUser_edit8 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("account/edit"))))
        

// @LINE:17
private[this] lazy val controllers_CtrlUser_submit9 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("account/submit"))))
        

// @LINE:20
private[this] lazy val controllers_CtrlEndPoint_add10 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("endpoints/add"))))
        

// @LINE:21
private[this] lazy val controllers_CtrlEndPoint_get11 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("endpoints/"),DynamicPart("id", """[^/]+"""))))
        

// @LINE:22
private[this] lazy val controllers_CtrlEndPoint_edit12 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("endpoints/"),DynamicPart("id", """[^/]+"""),StaticPart("/edit"))))
        

// @LINE:23
private[this] lazy val controllers_CtrlEndPoint_delete13 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("endpoints/"),DynamicPart("id", """[^/]+"""),StaticPart("/delete"))))
        

// @LINE:24
private[this] lazy val controllers_CtrlEndPoint_submit14 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("endpoints/"),DynamicPart("id", """[^/]+"""),StaticPart("/submit"))))
        

// @LINE:25
private[this] lazy val controllers_CtrlEndPoint_discover15 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("endpoints/"),DynamicPart("id", """[^/]+"""),StaticPart("/discover"))))
        

// @LINE:26
private[this] lazy val controllers_CtrlEndPoint_addResource16 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("endpoints/"),DynamicPart("id", """[^/]+"""),StaticPart("/addResource"))))
        

// @LINE:27
private[this] lazy val controllers_CtrlEndPoint_follow17 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("endpoints/"),DynamicPart("id", """[^/]+"""),StaticPart("/follow"))))
        

// @LINE:28
private[this] lazy val controllers_CtrlEndPoint_unfollow18 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("endpoints/"),DynamicPart("id", """[^/]+"""),StaticPart("/unfollow"))))
        

// @LINE:29
private[this] lazy val controllers_CtrlEndPoint_toggleFollow19 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("endpoints/"),DynamicPart("id", """[^/]+"""),StaticPart("/toggleFollow"))))
        

// @LINE:30
private[this] lazy val controllers_CtrlEndPoint_isFollowing20 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("endpoints/"),DynamicPart("id", """[^/]+"""),StaticPart("/isFollowing"))))
        

// @LINE:55
private[this] lazy val controllers_CtrlResource_get21 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("resources/"),DynamicPart("id", """[^/]+"""))))
        

// @LINE:56
private[this] lazy val controllers_CtrlResource_delete22 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("resources/"),DynamicPart("id", """[^/]+"""),StaticPart("/delete"))))
        

// @LINE:57
private[this] lazy val controllers_CtrlResource_delete23 = Route("DELETE", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("resources/"),DynamicPart("id", """[^/]+"""))))
        

// @LINE:58
private[this] lazy val controllers_CtrlResource_setPeriod24 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("resources/"),DynamicPart("id", """[^/]+"""),StaticPart("/setPeriod"))))
        

// @LINE:59
private[this] lazy val controllers_CtrlResource_setLabelName25 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("resources/"),DynamicPart("id", """[^/]+"""),StaticPart("/setLabel"))))
        

// @LINE:60
private[this] lazy val controllers_CtrlResource_setInputParser26 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("resources/"),DynamicPart("id", """[^/]+"""),StaticPart("/setInputParser"))))
        

// @LINE:61
private[this] lazy val controllers_CtrlResource_clearStream27 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("resources/"),DynamicPart("id", """[^/]+"""),StaticPart("/clearStream"))))
        

// @LINE:62
private[this] lazy val controllers_CtrlResource_follow28 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("resources/"),DynamicPart("id", """[^/]+"""),StaticPart("/follow"))))
        

// @LINE:63
private[this] lazy val controllers_CtrlResource_unfollow29 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("resources/"),DynamicPart("id", """[^/]+"""),StaticPart("/unfollow"))))
        

// @LINE:64
private[this] lazy val controllers_CtrlResource_toggleFollow30 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("resources/"),DynamicPart("id", """[^/]+"""),StaticPart("/togglefollow"))))
        

// @LINE:65
private[this] lazy val controllers_CtrlResource_isFollowing31 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("resources/"),DynamicPart("id", """[^/]+"""),StaticPart("/isFollowing"))))
        

// @LINE:66
private[this] lazy val controllers_CtrlResource_setPublicAccess32 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("resources/"),DynamicPart("id", """[^/]+"""),StaticPart("/setPublicAccess"))))
        

// @LINE:67
private[this] lazy val controllers_CtrlResource_removePublicAccess33 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("resources/"),DynamicPart("id", """[^/]+"""),StaticPart("/removePublicAccess"))))
        

// @LINE:68
private[this] lazy val controllers_CtrlResource_isPublicAccess34 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("resources/"),DynamicPart("id", """[^/]+"""),StaticPart("/isPublicAccess"))))
        

// @LINE:69
private[this] lazy val controllers_CtrlResource_togglePublicAccess35 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("resources/"),DynamicPart("id", """[^/]+"""),StaticPart("/togglePublicAccess"))))
        

// @LINE:70
private[this] lazy val controllers_CtrlResource_specifyPublicAccess36 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("resources/"),DynamicPart("id", """[^/]+"""),StaticPart("/specifyPublicAccess/"),DynamicPart("access", """[^/]+"""))))
        

// @LINE:73
private[this] lazy val controllers_Streams_getSecured37 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("streams/"),DynamicPart("user", """[^/]+"""),StaticPart("/"),DynamicPart("endpoint", """[^/]+"""),StaticPart("/"),DynamicPart("path", """.*"""))))
        

// @LINE:74
private[this] lazy val controllers_Streams_getSecured38 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("streams/"),DynamicPart("user", """[^/]+"""),StaticPart("/"),DynamicPart("endpoint", """[^/]+"""))))
        

// @LINE:75
private[this] lazy val controllers_Streams_post39 = Route("POST", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("streams/"),DynamicPart("user", """[^/]+"""),StaticPart("/"),DynamicPart("endpoint", """[^/]+"""),StaticPart("/"),DynamicPart("path", """.*"""))))
        

// @LINE:76
private[this] lazy val controllers_Streams_post40 = Route("POST", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("streams/"),DynamicPart("user", """[^/]+"""),StaticPart("/"),DynamicPart("endpoint", """[^/]+"""))))
        

// @LINE:79
private[this] lazy val controllers_Proxy_forwardByPath41 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("proxy/"),DynamicPart("user", """[^/]+"""),StaticPart("/"),DynamicPart("endpoint", """[^/]+"""),StaticPart("/"),DynamicPart("path", """.*"""))))
        

// @LINE:80
private[this] lazy val controllers_Proxy_forwardByPath42 = Route("POST", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("proxy/"),DynamicPart("user", """[^/]+"""),StaticPart("/"),DynamicPart("endpoint", """[^/]+"""),StaticPart("/"),DynamicPart("path", """.*"""))))
        

// @LINE:81
private[this] lazy val controllers_Proxy_forwardByPath43 = Route("PUT", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("proxy/"),DynamicPart("user", """[^/]+"""),StaticPart("/"),DynamicPart("endpoint", """[^/]+"""),StaticPart("/"),DynamicPart("path", """.*"""))))
        

// @LINE:82
private[this] lazy val controllers_Proxy_forwardByPath44 = Route("DELETE", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("proxy/"),DynamicPart("user", """[^/]+"""),StaticPart("/"),DynamicPart("endpoint", """[^/]+"""),StaticPart("/"),DynamicPart("path", """.*"""))))
        

// @LINE:84
private[this] lazy val controllers_Proxy_forwardById45 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("proxy/"),DynamicPart("id", """[^/]+"""))))
        

// @LINE:85
private[this] lazy val controllers_Proxy_forwardById46 = Route("POST", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("proxy/"),DynamicPart("id", """[^/]+"""))))
        

// @LINE:86
private[this] lazy val controllers_Proxy_forwardById47 = Route("PUT", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("proxy/"),DynamicPart("id", """[^/]+"""))))
        

// @LINE:87
private[this] lazy val controllers_Proxy_forwardById48 = Route("DELETE", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("proxy/"),DynamicPart("id", """[^/]+"""))))
        

// @LINE:97
private[this] lazy val controllers_DummyThing_discover49 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("dummy/"),DynamicPart("id", """[^/]+"""),StaticPart("/discover"))))
        

// @LINE:98
private[this] lazy val controllers_DummyThing_sensors50 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("dummy/"),DynamicPart("id", """[^/]+"""),StaticPart("/sensors"))))
        

// @LINE:99
private[this] lazy val controllers_DummyThing_temperature51 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("dummy/"),DynamicPart("id", """[^/]+"""),StaticPart("/sensors/temperature"))))
        

// @LINE:100
private[this] lazy val controllers_DummyThing_energy52 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("dummy/"),DynamicPart("id", """[^/]+"""),StaticPart("/sensors/energy"))))
        

// @LINE:101
private[this] lazy val controllers_DummyThing_print53 = Route("POST", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("dummy/"),DynamicPart("id", """[^/]+"""),StaticPart("/actuators/print"))))
        

// @LINE:104
private[this] lazy val controllers_Application_javascriptRoutes54 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("assets/javascripts/routes"))))
        

// @LINE:107
private[this] lazy val controllers_Assets_at55 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(Routes.defaultPrefix),StaticPart("assets/"),DynamicPart("file", """.+"""))))
        
def documentation = List(("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """authenticate""","""controllers.Login.authenticate(openid_identifier:String)"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """openIDCallback""","""controllers.Login.openIDCallback()"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """logout""","""controllers.Login.logout()"""),("""GET""", prefix,"""controllers.Application.home()"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """search""","""controllers.Application.search()"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """manage""","""controllers.Application.manage()"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """API""","""controllers.Public.about()"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """account""","""controllers.CtrlUser.get()"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """account/edit""","""controllers.CtrlUser.edit()"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """account/submit""","""controllers.CtrlUser.submit()"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """endpoints/add""","""controllers.CtrlEndPoint.add()"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """endpoints/$id<[^/]+>""","""controllers.CtrlEndPoint.get(id:Long)"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """endpoints/$id<[^/]+>/edit""","""controllers.CtrlEndPoint.edit(id:Long)"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """endpoints/$id<[^/]+>/delete""","""controllers.CtrlEndPoint.delete(id:Long)"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """endpoints/$id<[^/]+>/submit""","""controllers.CtrlEndPoint.submit(id:Long)"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """endpoints/$id<[^/]+>/discover""","""controllers.CtrlEndPoint.discover(id:Long)"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """endpoints/$id<[^/]+>/addResource""","""controllers.CtrlEndPoint.addResource(id:Long)"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """endpoints/$id<[^/]+>/follow""","""controllers.CtrlEndPoint.follow(id:Long)"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """endpoints/$id<[^/]+>/unfollow""","""controllers.CtrlEndPoint.unfollow(id:Long)"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """endpoints/$id<[^/]+>/toggleFollow""","""controllers.CtrlEndPoint.toggleFollow(id:Long)"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """endpoints/$id<[^/]+>/isFollowing""","""controllers.CtrlEndPoint.isFollowing(id:Long)"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """resources/$id<[^/]+>""","""controllers.CtrlResource.get(id:Long)"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """resources/$id<[^/]+>/delete""","""controllers.CtrlResource.delete(id:Long)"""),("""DELETE""", prefix + (if(prefix.endsWith("/")) "" else "/") + """resources/$id<[^/]+>""","""controllers.CtrlResource.delete(id:Long)"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """resources/$id<[^/]+>/setPeriod""","""controllers.CtrlResource.setPeriod(id:Long, period:Long)"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """resources/$id<[^/]+>/setLabel""","""controllers.CtrlResource.setLabelName(id:Long, label:String)"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """resources/$id<[^/]+>/setInputParser""","""controllers.CtrlResource.setInputParser(id:Long, parser:String)"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """resources/$id<[^/]+>/clearStream""","""controllers.CtrlResource.clearStream(id:Long)"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """resources/$id<[^/]+>/follow""","""controllers.CtrlResource.follow(id:Long)"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """resources/$id<[^/]+>/unfollow""","""controllers.CtrlResource.unfollow(id:Long)"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """resources/$id<[^/]+>/togglefollow""","""controllers.CtrlResource.toggleFollow(id:Long)"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """resources/$id<[^/]+>/isFollowing""","""controllers.CtrlResource.isFollowing(id:Long)"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """resources/$id<[^/]+>/setPublicAccess""","""controllers.CtrlResource.setPublicAccess(id:Long)"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """resources/$id<[^/]+>/removePublicAccess""","""controllers.CtrlResource.removePublicAccess(id:Long)"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """resources/$id<[^/]+>/isPublicAccess""","""controllers.CtrlResource.isPublicAccess(id:Long)"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """resources/$id<[^/]+>/togglePublicAccess""","""controllers.CtrlResource.togglePublicAccess(id:Long)"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """resources/$id<[^/]+>/specifyPublicAccess/$access<[^/]+>""","""controllers.CtrlResource.specifyPublicAccess(id:Long, access:Boolean)"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """streams/$user<[^/]+>/$endpoint<[^/]+>/$path<.*>""","""controllers.Streams.getSecured(user:String, endpoint:String, path:String, tail:Long ?= -1, last:Long ?= -1, since:Long ?= -1)"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """streams/$user<[^/]+>/$endpoint<[^/]+>""","""controllers.Streams.getSecured(user:String, endpoint:String, path:String ?= "", tail:Long ?= -1, last:Long ?= -1, since:Long ?= -1)"""),("""POST""", prefix + (if(prefix.endsWith("/")) "" else "/") + """streams/$user<[^/]+>/$endpoint<[^/]+>/$path<.*>""","""controllers.Streams.post(user:String, endpoint:String, path:String)"""),("""POST""", prefix + (if(prefix.endsWith("/")) "" else "/") + """streams/$user<[^/]+>/$endpoint<[^/]+>""","""controllers.Streams.post(user:String, endpoint:String, path:String ?= "")"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """proxy/$user<[^/]+>/$endpoint<[^/]+>/$path<.*>""","""controllers.Proxy.forwardByPath(user:String, endpoint:String, path:String)"""),("""POST""", prefix + (if(prefix.endsWith("/")) "" else "/") + """proxy/$user<[^/]+>/$endpoint<[^/]+>/$path<.*>""","""controllers.Proxy.forwardByPath(user:String, endpoint:String, path:String)"""),("""PUT""", prefix + (if(prefix.endsWith("/")) "" else "/") + """proxy/$user<[^/]+>/$endpoint<[^/]+>/$path<.*>""","""controllers.Proxy.forwardByPath(user:String, endpoint:String, path:String)"""),("""DELETE""", prefix + (if(prefix.endsWith("/")) "" else "/") + """proxy/$user<[^/]+>/$endpoint<[^/]+>/$path<.*>""","""controllers.Proxy.forwardByPath(user:String, endpoint:String, path:String)"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """proxy/$id<[^/]+>""","""controllers.Proxy.forwardById(id:Long, arguments:String)"""),("""POST""", prefix + (if(prefix.endsWith("/")) "" else "/") + """proxy/$id<[^/]+>""","""controllers.Proxy.forwardById(id:Long, arguments:String)"""),("""PUT""", prefix + (if(prefix.endsWith("/")) "" else "/") + """proxy/$id<[^/]+>""","""controllers.Proxy.forwardById(id:Long, arguments:String)"""),("""DELETE""", prefix + (if(prefix.endsWith("/")) "" else "/") + """proxy/$id<[^/]+>""","""controllers.Proxy.forwardById(id:Long, arguments:String)"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """dummy/$id<[^/]+>/discover""","""controllers.DummyThing.discover(id:Long)"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """dummy/$id<[^/]+>/sensors""","""controllers.DummyThing.sensors(id:Long)"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """dummy/$id<[^/]+>/sensors/temperature""","""controllers.DummyThing.temperature(id:Long)"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """dummy/$id<[^/]+>/sensors/energy""","""controllers.DummyThing.energy(id:Long)"""),("""POST""", prefix + (if(prefix.endsWith("/")) "" else "/") + """dummy/$id<[^/]+>/actuators/print""","""controllers.DummyThing.print(id:Long)"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """assets/javascripts/routes""","""controllers.Application.javascriptRoutes()"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """assets/$file<.+>""","""controllers.Assets.at(path:String = "/public", file:String)""")).foldLeft(List.empty[(String,String,String)]) { (s,e) => e match {
  case r @ (_,_,_) => s :+ r.asInstanceOf[(String,String,String)]
  case l => s ++ l.asInstanceOf[List[(String,String,String)]] 
}}
       
    
def routes:PartialFunction[RequestHeader,Handler] = {        

// @LINE:6
case controllers_Login_authenticate0(params) => {
   call(params.fromQuery[String]("openid_identifier", None)) { (openid_identifier) =>
        invokeHandler(controllers.Login.authenticate(openid_identifier), HandlerDef(this, "controllers.Login", "authenticate", Seq(classOf[String]),"GET", """ Pages""", Routes.prefix + """authenticate"""))
   }
}
        

// @LINE:7
case controllers_Login_openIDCallback1(params) => {
   call { 
        invokeHandler(controllers.Login.openIDCallback(), HandlerDef(this, "controllers.Login", "openIDCallback", Nil,"GET", """""", Routes.prefix + """openIDCallback"""))
   }
}
        

// @LINE:8
case controllers_Login_logout2(params) => {
   call { 
        invokeHandler(controllers.Login.logout(), HandlerDef(this, "controllers.Login", "logout", Nil,"GET", """""", Routes.prefix + """logout"""))
   }
}
        

// @LINE:9
case controllers_Application_home3(params) => {
   call { 
        invokeHandler(controllers.Application.home(), HandlerDef(this, "controllers.Application", "home", Nil,"GET", """""", Routes.prefix + """"""))
   }
}
        

// @LINE:10
case controllers_Application_search4(params) => {
   call { 
        invokeHandler(controllers.Application.search(), HandlerDef(this, "controllers.Application", "search", Nil,"GET", """""", Routes.prefix + """search"""))
   }
}
        

// @LINE:11
case controllers_Application_manage5(params) => {
   call { 
        invokeHandler(controllers.Application.manage(), HandlerDef(this, "controllers.Application", "manage", Nil,"GET", """""", Routes.prefix + """manage"""))
   }
}
        

// @LINE:12
case controllers_Public_about6(params) => {
   call { 
        invokeHandler(controllers.Public.about(), HandlerDef(this, "controllers.Public", "about", Nil,"GET", """""", Routes.prefix + """API"""))
   }
}
        

// @LINE:15
case controllers_CtrlUser_get7(params) => {
   call { 
        invokeHandler(controllers.CtrlUser.get(), HandlerDef(this, "controllers.CtrlUser", "get", Nil,"GET", """ Accounts""", Routes.prefix + """account"""))
   }
}
        

// @LINE:16
case controllers_CtrlUser_edit8(params) => {
   call { 
        invokeHandler(controllers.CtrlUser.edit(), HandlerDef(this, "controllers.CtrlUser", "edit", Nil,"GET", """""", Routes.prefix + """account/edit"""))
   }
}
        

// @LINE:17
case controllers_CtrlUser_submit9(params) => {
   call { 
        invokeHandler(controllers.CtrlUser.submit(), HandlerDef(this, "controllers.CtrlUser", "submit", Nil,"GET", """""", Routes.prefix + """account/submit"""))
   }
}
        

// @LINE:20
case controllers_CtrlEndPoint_add10(params) => {
   call { 
        invokeHandler(controllers.CtrlEndPoint.add(), HandlerDef(this, "controllers.CtrlEndPoint", "add", Nil,"GET", """ End points""", Routes.prefix + """endpoints/add"""))
   }
}
        

// @LINE:21
case controllers_CtrlEndPoint_get11(params) => {
   call(params.fromPath[Long]("id", None)) { (id) =>
        invokeHandler(controllers.CtrlEndPoint.get(id), HandlerDef(this, "controllers.CtrlEndPoint", "get", Seq(classOf[Long]),"GET", """""", Routes.prefix + """endpoints/$id<[^/]+>"""))
   }
}
        

// @LINE:22
case controllers_CtrlEndPoint_edit12(params) => {
   call(params.fromPath[Long]("id", None)) { (id) =>
        invokeHandler(controllers.CtrlEndPoint.edit(id), HandlerDef(this, "controllers.CtrlEndPoint", "edit", Seq(classOf[Long]),"GET", """""", Routes.prefix + """endpoints/$id<[^/]+>/edit"""))
   }
}
        

// @LINE:23
case controllers_CtrlEndPoint_delete13(params) => {
   call(params.fromPath[Long]("id", None)) { (id) =>
        invokeHandler(controllers.CtrlEndPoint.delete(id), HandlerDef(this, "controllers.CtrlEndPoint", "delete", Seq(classOf[Long]),"GET", """""", Routes.prefix + """endpoints/$id<[^/]+>/delete"""))
   }
}
        

// @LINE:24
case controllers_CtrlEndPoint_submit14(params) => {
   call(params.fromPath[Long]("id", None)) { (id) =>
        invokeHandler(controllers.CtrlEndPoint.submit(id), HandlerDef(this, "controllers.CtrlEndPoint", "submit", Seq(classOf[Long]),"GET", """""", Routes.prefix + """endpoints/$id<[^/]+>/submit"""))
   }
}
        

// @LINE:25
case controllers_CtrlEndPoint_discover15(params) => {
   call(params.fromPath[Long]("id", None)) { (id) =>
        invokeHandler(controllers.CtrlEndPoint.discover(id), HandlerDef(this, "controllers.CtrlEndPoint", "discover", Seq(classOf[Long]),"GET", """""", Routes.prefix + """endpoints/$id<[^/]+>/discover"""))
   }
}
        

// @LINE:26
case controllers_CtrlEndPoint_addResource16(params) => {
   call(params.fromPath[Long]("id", None)) { (id) =>
        invokeHandler(controllers.CtrlEndPoint.addResource(id), HandlerDef(this, "controllers.CtrlEndPoint", "addResource", Seq(classOf[Long]),"GET", """""", Routes.prefix + """endpoints/$id<[^/]+>/addResource"""))
   }
}
        

// @LINE:27
case controllers_CtrlEndPoint_follow17(params) => {
   call(params.fromPath[Long]("id", None)) { (id) =>
        invokeHandler(controllers.CtrlEndPoint.follow(id), HandlerDef(this, "controllers.CtrlEndPoint", "follow", Seq(classOf[Long]),"GET", """""", Routes.prefix + """endpoints/$id<[^/]+>/follow"""))
   }
}
        

// @LINE:28
case controllers_CtrlEndPoint_unfollow18(params) => {
   call(params.fromPath[Long]("id", None)) { (id) =>
        invokeHandler(controllers.CtrlEndPoint.unfollow(id), HandlerDef(this, "controllers.CtrlEndPoint", "unfollow", Seq(classOf[Long]),"GET", """""", Routes.prefix + """endpoints/$id<[^/]+>/unfollow"""))
   }
}
        

// @LINE:29
case controllers_CtrlEndPoint_toggleFollow19(params) => {
   call(params.fromPath[Long]("id", None)) { (id) =>
        invokeHandler(controllers.CtrlEndPoint.toggleFollow(id), HandlerDef(this, "controllers.CtrlEndPoint", "toggleFollow", Seq(classOf[Long]),"GET", """""", Routes.prefix + """endpoints/$id<[^/]+>/toggleFollow"""))
   }
}
        

// @LINE:30
case controllers_CtrlEndPoint_isFollowing20(params) => {
   call(params.fromPath[Long]("id", None)) { (id) =>
        invokeHandler(controllers.CtrlEndPoint.isFollowing(id), HandlerDef(this, "controllers.CtrlEndPoint", "isFollowing", Seq(classOf[Long]),"GET", """""", Routes.prefix + """endpoints/$id<[^/]+>/isFollowing"""))
   }
}
        

// @LINE:55
case controllers_CtrlResource_get21(params) => {
   call(params.fromPath[Long]("id", None)) { (id) =>
        invokeHandler(controllers.CtrlResource.get(id), HandlerDef(this, "controllers.CtrlResource", "get", Seq(classOf[Long]),"GET", """ Resources
 with ID shown in the url""", Routes.prefix + """resources/$id<[^/]+>"""))
   }
}
        

// @LINE:56
case controllers_CtrlResource_delete22(params) => {
   call(params.fromPath[Long]("id", None)) { (id) =>
        invokeHandler(controllers.CtrlResource.delete(id), HandlerDef(this, "controllers.CtrlResource", "delete", Seq(classOf[Long]),"GET", """""", Routes.prefix + """resources/$id<[^/]+>/delete"""))
   }
}
        

// @LINE:57
case controllers_CtrlResource_delete23(params) => {
   call(params.fromPath[Long]("id", None)) { (id) =>
        invokeHandler(controllers.CtrlResource.delete(id), HandlerDef(this, "controllers.CtrlResource", "delete", Seq(classOf[Long]),"DELETE", """""", Routes.prefix + """resources/$id<[^/]+>"""))
   }
}
        

// @LINE:58
case controllers_CtrlResource_setPeriod24(params) => {
   call(params.fromPath[Long]("id", None), params.fromQuery[Long]("period", None)) { (id, period) =>
        invokeHandler(controllers.CtrlResource.setPeriod(id, period), HandlerDef(this, "controllers.CtrlResource", "setPeriod", Seq(classOf[Long], classOf[Long]),"GET", """""", Routes.prefix + """resources/$id<[^/]+>/setPeriod"""))
   }
}
        

// @LINE:59
case controllers_CtrlResource_setLabelName25(params) => {
   call(params.fromPath[Long]("id", None), params.fromQuery[String]("label", None)) { (id, label) =>
        invokeHandler(controllers.CtrlResource.setLabelName(id, label), HandlerDef(this, "controllers.CtrlResource", "setLabelName", Seq(classOf[Long], classOf[String]),"GET", """""", Routes.prefix + """resources/$id<[^/]+>/setLabel"""))
   }
}
        

// @LINE:60
case controllers_CtrlResource_setInputParser26(params) => {
   call(params.fromPath[Long]("id", None), params.fromQuery[String]("parser", None)) { (id, parser) =>
        invokeHandler(controllers.CtrlResource.setInputParser(id, parser), HandlerDef(this, "controllers.CtrlResource", "setInputParser", Seq(classOf[Long], classOf[String]),"GET", """""", Routes.prefix + """resources/$id<[^/]+>/setInputParser"""))
   }
}
        

// @LINE:61
case controllers_CtrlResource_clearStream27(params) => {
   call(params.fromPath[Long]("id", None)) { (id) =>
        invokeHandler(controllers.CtrlResource.clearStream(id), HandlerDef(this, "controllers.CtrlResource", "clearStream", Seq(classOf[Long]),"GET", """""", Routes.prefix + """resources/$id<[^/]+>/clearStream"""))
   }
}
        

// @LINE:62
case controllers_CtrlResource_follow28(params) => {
   call(params.fromPath[Long]("id", None)) { (id) =>
        invokeHandler(controllers.CtrlResource.follow(id), HandlerDef(this, "controllers.CtrlResource", "follow", Seq(classOf[Long]),"GET", """""", Routes.prefix + """resources/$id<[^/]+>/follow"""))
   }
}
        

// @LINE:63
case controllers_CtrlResource_unfollow29(params) => {
   call(params.fromPath[Long]("id", None)) { (id) =>
        invokeHandler(controllers.CtrlResource.unfollow(id), HandlerDef(this, "controllers.CtrlResource", "unfollow", Seq(classOf[Long]),"GET", """""", Routes.prefix + """resources/$id<[^/]+>/unfollow"""))
   }
}
        

// @LINE:64
case controllers_CtrlResource_toggleFollow30(params) => {
   call(params.fromPath[Long]("id", None)) { (id) =>
        invokeHandler(controllers.CtrlResource.toggleFollow(id), HandlerDef(this, "controllers.CtrlResource", "toggleFollow", Seq(classOf[Long]),"GET", """""", Routes.prefix + """resources/$id<[^/]+>/togglefollow"""))
   }
}
        

// @LINE:65
case controllers_CtrlResource_isFollowing31(params) => {
   call(params.fromPath[Long]("id", None)) { (id) =>
        invokeHandler(controllers.CtrlResource.isFollowing(id), HandlerDef(this, "controllers.CtrlResource", "isFollowing", Seq(classOf[Long]),"GET", """""", Routes.prefix + """resources/$id<[^/]+>/isFollowing"""))
   }
}
        

// @LINE:66
case controllers_CtrlResource_setPublicAccess32(params) => {
   call(params.fromPath[Long]("id", None)) { (id) =>
        invokeHandler(controllers.CtrlResource.setPublicAccess(id), HandlerDef(this, "controllers.CtrlResource", "setPublicAccess", Seq(classOf[Long]),"GET", """""", Routes.prefix + """resources/$id<[^/]+>/setPublicAccess"""))
   }
}
        

// @LINE:67
case controllers_CtrlResource_removePublicAccess33(params) => {
   call(params.fromPath[Long]("id", None)) { (id) =>
        invokeHandler(controllers.CtrlResource.removePublicAccess(id), HandlerDef(this, "controllers.CtrlResource", "removePublicAccess", Seq(classOf[Long]),"GET", """""", Routes.prefix + """resources/$id<[^/]+>/removePublicAccess"""))
   }
}
        

// @LINE:68
case controllers_CtrlResource_isPublicAccess34(params) => {
   call(params.fromPath[Long]("id", None)) { (id) =>
        invokeHandler(controllers.CtrlResource.isPublicAccess(id), HandlerDef(this, "controllers.CtrlResource", "isPublicAccess", Seq(classOf[Long]),"GET", """""", Routes.prefix + """resources/$id<[^/]+>/isPublicAccess"""))
   }
}
        

// @LINE:69
case controllers_CtrlResource_togglePublicAccess35(params) => {
   call(params.fromPath[Long]("id", None)) { (id) =>
        invokeHandler(controllers.CtrlResource.togglePublicAccess(id), HandlerDef(this, "controllers.CtrlResource", "togglePublicAccess", Seq(classOf[Long]),"GET", """""", Routes.prefix + """resources/$id<[^/]+>/togglePublicAccess"""))
   }
}
        

// @LINE:70
case controllers_CtrlResource_specifyPublicAccess36(params) => {
   call(params.fromPath[Long]("id", None), params.fromPath[Boolean]("access", None)) { (id, access) =>
        invokeHandler(controllers.CtrlResource.specifyPublicAccess(id, access), HandlerDef(this, "controllers.CtrlResource", "specifyPublicAccess", Seq(classOf[Long], classOf[Boolean]),"GET", """""", Routes.prefix + """resources/$id<[^/]+>/specifyPublicAccess/$access<[^/]+>"""))
   }
}
        

// @LINE:73
case controllers_Streams_getSecured37(params) => {
   call(params.fromPath[String]("user", None), params.fromPath[String]("endpoint", None), params.fromPath[String]("path", None), params.fromQuery[Long]("tail", Some(-1)), params.fromQuery[Long]("last", Some(-1)), params.fromQuery[Long]("since", Some(-1))) { (user, endpoint, path, tail, last, since) =>
        invokeHandler(controllers.Streams.getSecured(user, endpoint, path, tail, last, since), HandlerDef(this, "controllers.Streams", "getSecured", Seq(classOf[String], classOf[String], classOf[String], classOf[Long], classOf[Long], classOf[Long]),"GET", """ Streams""", Routes.prefix + """streams/$user<[^/]+>/$endpoint<[^/]+>/$path<.*>"""))
   }
}
        

// @LINE:74
case controllers_Streams_getSecured38(params) => {
   call(params.fromPath[String]("user", None), params.fromPath[String]("endpoint", None), params.fromQuery[String]("path", Some("")), params.fromQuery[Long]("tail", Some(-1)), params.fromQuery[Long]("last", Some(-1)), params.fromQuery[Long]("since", Some(-1))) { (user, endpoint, path, tail, last, since) =>
        invokeHandler(controllers.Streams.getSecured(user, endpoint, path, tail, last, since), HandlerDef(this, "controllers.Streams", "getSecured", Seq(classOf[String], classOf[String], classOf[String], classOf[Long], classOf[Long], classOf[Long]),"GET", """""", Routes.prefix + """streams/$user<[^/]+>/$endpoint<[^/]+>"""))
   }
}
        

// @LINE:75
case controllers_Streams_post39(params) => {
   call(params.fromPath[String]("user", None), params.fromPath[String]("endpoint", None), params.fromPath[String]("path", None)) { (user, endpoint, path) =>
        invokeHandler(controllers.Streams.post(user, endpoint, path), HandlerDef(this, "controllers.Streams", "post", Seq(classOf[String], classOf[String], classOf[String]),"POST", """""", Routes.prefix + """streams/$user<[^/]+>/$endpoint<[^/]+>/$path<.*>"""))
   }
}
        

// @LINE:76
case controllers_Streams_post40(params) => {
   call(params.fromPath[String]("user", None), params.fromPath[String]("endpoint", None), params.fromQuery[String]("path", Some(""))) { (user, endpoint, path) =>
        invokeHandler(controllers.Streams.post(user, endpoint, path), HandlerDef(this, "controllers.Streams", "post", Seq(classOf[String], classOf[String], classOf[String]),"POST", """""", Routes.prefix + """streams/$user<[^/]+>/$endpoint<[^/]+>"""))
   }
}
        

// @LINE:79
case controllers_Proxy_forwardByPath41(params) => {
   call(params.fromPath[String]("user", None), params.fromPath[String]("endpoint", None), params.fromPath[String]("path", None)) { (user, endpoint, path) =>
        invokeHandler(controllers.Proxy.forwardByPath(user, endpoint, path), HandlerDef(this, "controllers.Proxy", "forwardByPath", Seq(classOf[String], classOf[String], classOf[String]),"GET", """ Proxy""", Routes.prefix + """proxy/$user<[^/]+>/$endpoint<[^/]+>/$path<.*>"""))
   }
}
        

// @LINE:80
case controllers_Proxy_forwardByPath42(params) => {
   call(params.fromPath[String]("user", None), params.fromPath[String]("endpoint", None), params.fromPath[String]("path", None)) { (user, endpoint, path) =>
        invokeHandler(controllers.Proxy.forwardByPath(user, endpoint, path), HandlerDef(this, "controllers.Proxy", "forwardByPath", Seq(classOf[String], classOf[String], classOf[String]),"POST", """""", Routes.prefix + """proxy/$user<[^/]+>/$endpoint<[^/]+>/$path<.*>"""))
   }
}
        

// @LINE:81
case controllers_Proxy_forwardByPath43(params) => {
   call(params.fromPath[String]("user", None), params.fromPath[String]("endpoint", None), params.fromPath[String]("path", None)) { (user, endpoint, path) =>
        invokeHandler(controllers.Proxy.forwardByPath(user, endpoint, path), HandlerDef(this, "controllers.Proxy", "forwardByPath", Seq(classOf[String], classOf[String], classOf[String]),"PUT", """""", Routes.prefix + """proxy/$user<[^/]+>/$endpoint<[^/]+>/$path<.*>"""))
   }
}
        

// @LINE:82
case controllers_Proxy_forwardByPath44(params) => {
   call(params.fromPath[String]("user", None), params.fromPath[String]("endpoint", None), params.fromPath[String]("path", None)) { (user, endpoint, path) =>
        invokeHandler(controllers.Proxy.forwardByPath(user, endpoint, path), HandlerDef(this, "controllers.Proxy", "forwardByPath", Seq(classOf[String], classOf[String], classOf[String]),"DELETE", """""", Routes.prefix + """proxy/$user<[^/]+>/$endpoint<[^/]+>/$path<.*>"""))
   }
}
        

// @LINE:84
case controllers_Proxy_forwardById45(params) => {
   call(params.fromPath[Long]("id", None), params.fromQuery[String]("arguments", None)) { (id, arguments) =>
        invokeHandler(controllers.Proxy.forwardById(id, arguments), HandlerDef(this, "controllers.Proxy", "forwardById", Seq(classOf[Long], classOf[String]),"GET", """""", Routes.prefix + """proxy/$id<[^/]+>"""))
   }
}
        

// @LINE:85
case controllers_Proxy_forwardById46(params) => {
   call(params.fromPath[Long]("id", None), params.fromQuery[String]("arguments", None)) { (id, arguments) =>
        invokeHandler(controllers.Proxy.forwardById(id, arguments), HandlerDef(this, "controllers.Proxy", "forwardById", Seq(classOf[Long], classOf[String]),"POST", """""", Routes.prefix + """proxy/$id<[^/]+>"""))
   }
}
        

// @LINE:86
case controllers_Proxy_forwardById47(params) => {
   call(params.fromPath[Long]("id", None), params.fromQuery[String]("arguments", None)) { (id, arguments) =>
        invokeHandler(controllers.Proxy.forwardById(id, arguments), HandlerDef(this, "controllers.Proxy", "forwardById", Seq(classOf[Long], classOf[String]),"PUT", """""", Routes.prefix + """proxy/$id<[^/]+>"""))
   }
}
        

// @LINE:87
case controllers_Proxy_forwardById48(params) => {
   call(params.fromPath[Long]("id", None), params.fromQuery[String]("arguments", None)) { (id, arguments) =>
        invokeHandler(controllers.Proxy.forwardById(id, arguments), HandlerDef(this, "controllers.Proxy", "forwardById", Seq(classOf[Long], classOf[String]),"DELETE", """""", Routes.prefix + """proxy/$id<[^/]+>"""))
   }
}
        

// @LINE:97
case controllers_DummyThing_discover49(params) => {
   call(params.fromPath[Long]("id", None)) { (id) =>
        invokeHandler(controllers.DummyThing.discover(id), HandlerDef(this, "controllers.DummyThing", "discover", Seq(classOf[Long]),"GET", """ Dummy thing""", Routes.prefix + """dummy/$id<[^/]+>/discover"""))
   }
}
        

// @LINE:98
case controllers_DummyThing_sensors50(params) => {
   call(params.fromPath[Long]("id", None)) { (id) =>
        invokeHandler(controllers.DummyThing.sensors(id), HandlerDef(this, "controllers.DummyThing", "sensors", Seq(classOf[Long]),"GET", """""", Routes.prefix + """dummy/$id<[^/]+>/sensors"""))
   }
}
        

// @LINE:99
case controllers_DummyThing_temperature51(params) => {
   call(params.fromPath[Long]("id", None)) { (id) =>
        invokeHandler(controllers.DummyThing.temperature(id), HandlerDef(this, "controllers.DummyThing", "temperature", Seq(classOf[Long]),"GET", """""", Routes.prefix + """dummy/$id<[^/]+>/sensors/temperature"""))
   }
}
        

// @LINE:100
case controllers_DummyThing_energy52(params) => {
   call(params.fromPath[Long]("id", None)) { (id) =>
        invokeHandler(controllers.DummyThing.energy(id), HandlerDef(this, "controllers.DummyThing", "energy", Seq(classOf[Long]),"GET", """""", Routes.prefix + """dummy/$id<[^/]+>/sensors/energy"""))
   }
}
        

// @LINE:101
case controllers_DummyThing_print53(params) => {
   call(params.fromPath[Long]("id", None)) { (id) =>
        invokeHandler(controllers.DummyThing.print(id), HandlerDef(this, "controllers.DummyThing", "print", Seq(classOf[Long]),"POST", """""", Routes.prefix + """dummy/$id<[^/]+>/actuators/print"""))
   }
}
        

// @LINE:104
case controllers_Application_javascriptRoutes54(params) => {
   call { 
        invokeHandler(controllers.Application.javascriptRoutes(), HandlerDef(this, "controllers.Application", "javascriptRoutes", Nil,"GET", """ Javascript Routes""", Routes.prefix + """assets/javascripts/routes"""))
   }
}
        

// @LINE:107
case controllers_Assets_at55(params) => {
   call(Param[String]("path", Right("/public")), params.fromPath[String]("file", None)) { (path, file) =>
        invokeHandler(controllers.Assets.at(path, file), HandlerDef(this, "controllers.Assets", "at", Seq(classOf[String], classOf[String]),"GET", """ Map static resources from the /public folder to the /assets URL path""", Routes.prefix + """assets/$file<.+>"""))
   }
}
        
}
    
}
        