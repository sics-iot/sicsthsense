
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
object homePage extends BaseScalaTemplate[play.api.templates.Html,Format[play.api.templates.Html]](play.api.templates.HtmlFormat) with play.api.templates.Template0[play.api.templates.Html] {

    /**/
    def apply():play.api.templates.Html = {
        _display_ {import helper._

def /*3.2*/showFollowed/*3.14*/(endPoints: List[EndPoint], resources: List[Resource]):play.api.templates.Html = {_display_(

Seq[Any](format.raw/*3.72*/("""

  """),_display_(Seq[Any](/*5.4*/vendpoint/*5.13*/.list/*5.18*/{_display_(Seq[Any](format.raw/*5.19*/("""<h1>End points you follow</h1>""")))}/*5.50*/(CtrlUser.getUser().followedEndPoints()))),format.raw/*5.90*/("""

  """),_display_(Seq[Any](/*7.4*/vresource/*7.13*/.listStreams/*7.25*/{_display_(Seq[Any](format.raw/*7.26*/("""<h1>Resources you follow</h1>""")))}/*7.56*/(CtrlUser.getUser().followedResources()))),format.raw/*7.96*/("""
  
  """),_display_(Seq[Any](/*9.4*/if(endPoints.size == 0 && resources.size == 0)/*9.50*/ {_display_(Seq[Any](format.raw/*9.52*/("""
    You don't follow any end point nor resource yet. <a href=""""),_display_(Seq[Any](/*10.64*/routes/*10.70*/.Application.search())),format.raw/*10.91*/("""">Search</a> public resources or <a href=""""),_display_(Seq[Any](/*10.134*/routes/*10.140*/.Application.manage())),format.raw/*10.161*/("""">add yours</a> to get started!<p/>
  """)))})),format.raw/*11.4*/("""
""")))};
Seq[Any](format.raw/*2.1*/("""
"""),format.raw/*12.2*/("""

"""),_display_(Seq[Any](/*14.2*/layout("Home", session)/*14.25*/ {_display_(Seq[Any](format.raw/*14.27*/("""

  """),_display_(Seq[Any](/*16.4*/showFollowed(CtrlUser.getUser().followedEndPoints(), CtrlUser.getUser().followedResources()))),format.raw/*16.96*/(""" 

""")))})),format.raw/*18.2*/("""
"""))}
    }
    
    def render(): play.api.templates.Html = apply()
    
    def f:(() => play.api.templates.Html) = () => apply()
    
    def ref: this.type = this

}
                /*
                    -- GENERATED --
                    DATE: Mon Feb 11 23:20:20 CET 2013
                    SOURCE: /Users/ljm/code/SicsthSense/cloud-altmanage/app/views/homePage.scala.html
                    HASH: 5c88fcaf89205e9aa58f44df359ffceffb84cd02
                    MATRIX: 790->19|810->31|931->89|970->94|987->103|1000->108|1038->109|1087->140|1148->180|1187->185|1204->194|1224->206|1262->207|1310->237|1371->277|1412->284|1466->330|1505->332|1605->396|1620->402|1663->423|1743->466|1759->472|1803->493|1873->532|1913->17|1941->534|1979->537|2011->560|2051->562|2091->567|2205->659|2240->663
                    LINES: 29->3|29->3|31->3|33->5|33->5|33->5|33->5|33->5|33->5|35->7|35->7|35->7|35->7|35->7|35->7|37->9|37->9|37->9|38->10|38->10|38->10|38->10|38->10|38->10|39->11|41->2|42->12|44->14|44->14|44->14|46->16|46->16|48->18
                    -- GENERATED --
                */
            