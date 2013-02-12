
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
object errorPage extends BaseScalaTemplate[play.api.templates.Html,Format[play.api.templates.Html]](play.api.templates.HtmlFormat) with play.api.templates.Template1[String,play.api.templates.Html] {

    /**/
    def apply/*1.2*/(msg: String):play.api.templates.Html = {
        _display_ {import helper._

import controllers.Secured;

def /*5.2*/showFollowed/*5.14*/(endPoints: List[EndPoint], resources: List[Resource]):play.api.templates.Html = {_display_(

Seq[Any](format.raw/*5.72*/("""
	  """),_display_(Seq[Any](/*6.5*/vendpoint/*6.14*/.list/*6.19*/{_display_(Seq[Any](format.raw/*6.20*/("""<h1>End points you follow</h1>""")))}/*6.51*/(CtrlUser.getUser().followedEndPoints()))),format.raw/*6.91*/("""
	
	  """),_display_(Seq[Any](/*8.5*/vresource/*8.14*/.listStreams/*8.26*/{_display_(Seq[Any](format.raw/*8.27*/("""<h1>Resources you follow</h1>""")))}/*8.57*/(CtrlUser.getUser().followedResources()))),format.raw/*8.97*/("""
	  
	  """),_display_(Seq[Any](/*10.5*/if(endPoints.size == 0 && resources.size == 0)/*10.51*/ {_display_(Seq[Any](format.raw/*10.53*/("""
	    You don't follow any end point nor resource yet. <a href=""""),_display_(Seq[Any](/*11.65*/routes/*11.71*/.Application.search())),format.raw/*11.92*/("""">Search</a> public resources or <a href=""""),_display_(Seq[Any](/*11.135*/routes/*11.141*/.Application.manage())),format.raw/*11.162*/("""">add yours</a> to get started!<p/>
	  """)))})),format.raw/*12.5*/("""
""")))};
Seq[Any](format.raw/*1.15*/("""
"""),format.raw/*4.1*/("""
"""),format.raw/*13.2*/("""

"""),_display_(Seq[Any](/*15.2*/layout("Error!", session)/*15.27*/ {_display_(Seq[Any](format.raw/*15.29*/("""
	<div class="alert alert-block alert-error">
    <button type="button" class="close" data-dismiss="alert">&times;</button>
    <strong>Error!</strong>"""),_display_(Seq[Any](/*18.29*/msg)),format.raw/*18.32*/("""
  </div>
  
  """),_display_(Seq[Any](/*21.4*/if(actions.CheckPermissionsAction.getUsername() != null)/*21.60*/ {_display_(Seq[Any](format.raw/*21.62*/("""   
  	"""),_display_(Seq[Any](/*22.5*/showFollowed(CtrlUser.getUser().followedEndPoints(), CtrlUser.getUser().followedResources()))),format.raw/*22.97*/(""" 
	""")))})),format.raw/*23.3*/("""
""")))})),format.raw/*24.2*/("""
"""))}
    }
    
    def render(msg:String): play.api.templates.Html = apply(msg)
    
    def f:((String) => play.api.templates.Html) = (msg) => apply(msg)
    
    def ref: this.type = this

}
                /*
                    -- GENERATED --
                    DATE: Mon Feb 11 23:20:20 CET 2013
                    SOURCE: /Users/ljm/code/SicsthSense/cloud-altmanage/app/views/errorPage.scala.html
                    HASH: 2d7ba51ddafa907df777822df70685dcc7128c9a
                    MATRIX: 727->1|845->63|865->75|986->133|1025->138|1042->147|1055->152|1093->153|1142->184|1203->224|1244->231|1261->240|1281->252|1319->253|1367->283|1428->323|1472->332|1527->378|1567->380|1668->445|1683->451|1726->472|1806->515|1822->521|1866->542|1937->582|1978->14|2005->61|2033->584|2071->587|2105->612|2145->614|2333->766|2358->769|2409->785|2474->841|2514->843|2557->851|2671->943|2706->947|2739->949
                    LINES: 26->1|31->5|31->5|33->5|34->6|34->6|34->6|34->6|34->6|34->6|36->8|36->8|36->8|36->8|36->8|36->8|38->10|38->10|38->10|39->11|39->11|39->11|39->11|39->11|39->11|40->12|42->1|43->4|44->13|46->15|46->15|46->15|49->18|49->18|52->21|52->21|52->21|53->22|53->22|54->23|55->24
                    -- GENERATED --
                */
            