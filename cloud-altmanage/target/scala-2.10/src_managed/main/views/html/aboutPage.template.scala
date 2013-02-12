
package views.html

import play.templates._
import play.templates.TemplateMagic._

import play.api.templates._
import play.api.templates.PlayMagic._
import models._
import controllers._
import java.lang._
import java.util._
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import play.api.i18n._
import play.core.j.PlayMagicForJava._
import play.mvc._
import play.data._
import play.api.data.Field
import play.mvc.Http.Context.Implicit._
import views.html._
/**/
object aboutPage extends BaseScalaTemplate[play.api.templates.Html,Format[play.api.templates.Html]](play.api.templates.HtmlFormat) with play.api.templates.Template0[play.api.templates.Html] {

    /**/
    def apply():play.api.templates.Html = {
        _display_ {import helper._


Seq[Any](format.raw/*2.1*/("""
"""),_display_(Seq[Any](/*3.2*/layout("About", session)/*3.26*/ {_display_(Seq[Any](format.raw/*3.28*/("""

  <h1>Getting started</h1>
  
  The first step is to create an account. Then, you can add your devices and start collecting their data.
      
  Devices can provide a description of the resources they offer. This description must be hosted on the device at <code>/discover</code>, and have the following format:
  
  <pre>
  """),format.raw/*12.3*/("""{"""),format.raw/*12.4*/("""
    "uid": "uid",
    "resources": [
      "/sensors/light",
      "/sensors/energy"
    ]
  """),format.raw/*18.3*/("""}"""),format.raw/*18.4*/("""</pre>
   
  <h1>Polling data</h1>
  
  The easiest way to get your sensor data to the Sics<sup>th</sup>Sense is to let is poll your device periodically.
  This is solution is very flexible because it entierly configured in the Cloud, through the Web interface.
  After creating your device, just discover or add new resources manually, and set a polling interval.
  The data will soon appear in graphs, and be available through our RESTful API.

  <h1>Posting data</h1>
  
  Posting to Sics<sup>th</sup>Sense is also useful, for delay-sensitive notification, or for data collection from devices with no public URL (e.g. a smartphone).
  To do so, just post at the following URL:
  <pre>
  http://sense.sics.se/streams/[user]/[device]/[path]</pre>
  
  The data can be either sent as a plain text integer or float, or as a Json structure representing a resource tree.
  For instance, to post samples for <code>/sensors/light</code> and <code>/sensors/energy</code>, one may post the following <code>application/json</code> data: 
  <pre>
  """),format.raw/*37.3*/("""{"""),format.raw/*37.4*/("""
    "sensors": """),format.raw/*38.16*/("""{"""),format.raw/*38.17*/("""
      "light": 19,
      "energy": 42
    """),format.raw/*41.5*/("""}"""),format.raw/*41.6*/("""
  """),format.raw/*42.3*/("""}"""),format.raw/*42.4*/("""</pre>
  
  Non existing devices and resources you post to will be created on-the-fly.
  
  <h1>Getting data</h1>
  
  You can fetch data from Sics<sup>th</sup>Sense's database through a <code>GET</code> at the following URI:
  <pre>
  http://sense.sics.se/streams/[user]/[device]/[path]</pre>
  
  URI arguments allow to specify which subset of the dataset you want to get. Use <code>?tail=n</code> to get the <code>n</code> most recent samples. Use <code>?last=t</code>
  to get samples posted during the last <code>t</code> seconds. Use <code>?since=t</code> to get the samples posted since (including) absolute UNIX time <code>t</code> (in seconds).
  
  <h1>Interacting with devices</h1>
  
  Sics<sup>th</sup>Sense can act as a RESTful proxy towards any public device. This allows to share your devices without making their actual URI public.
  Just <code>GET</code>, <code>POST</code>, <code>PUT</code> or <code>DELETE</code> to: 
  
  <pre>
  http://sense.sics.se/proxy/[user]/[device]/[path]</pre>
  
  <h1>Inspecting Users, End points and Resources</h1>
  
  You can access a subset of the data on users, devices and resources, in Json format.
  Simply get the following URIs:
  <pre>
  GET        http://sense.sics.se/users/[user]
  GET        http://sense.sics.se/endpoints/[user]/[device]
  GET        http://sense.sics.se/resources/[user]/[device]/[path]</pre>
   
""")))})),format.raw/*72.2*/("""
"""))}
    }
    
    def render(): play.api.templates.Html = apply()
    
    def f:(() => play.api.templates.Html) = () => apply()
    
    def ref: this.type = this

}
                /*
                    -- GENERATED --
                    DATE: Mon Feb 11 23:20:20 CET 2013
                    SOURCE: /Users/ljm/code/SicsthSense/cloud-altmanage/app/views/aboutPage.scala.html
                    HASH: e78d94aa27f7bf978333ee7e5b59bd4ee1d8253b
                    MATRIX: 807->17|843->19|875->43|914->45|1268->372|1296->373|1417->467|1445->468|2512->1508|2540->1509|2584->1525|2613->1526|2683->1569|2711->1570|2741->1573|2769->1574|4180->2954
                    LINES: 30->2|31->3|31->3|31->3|40->12|40->12|46->18|46->18|65->37|65->37|66->38|66->38|69->41|69->41|70->42|70->42|100->72
                    -- GENERATED --
                */
            