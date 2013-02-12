
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
object configuration extends BaseScalaTemplate[play.api.templates.Html,Format[play.api.templates.Html]](play.api.templates.HtmlFormat) with play.api.templates.Template1[List[EndPoint],play.api.templates.Html] {

    /**/
    def apply/*1.2*/(endPoints: List[EndPoint]):play.api.templates.Html = {
        _display_ {import helper._


Seq[Any](format.raw/*1.29*/("""

"""),format.raw/*4.1*/("""
"""),_display_(Seq[Any](/*5.2*/layout("Manage", session)/*5.27*/ {_display_(Seq[Any](format.raw/*5.29*/("""
 
  <h1>Register new device</h1>
 
   """),_display_(Seq[Any](/*9.5*/helper/*9.11*/.form(action = routes.CtrlEndPoint.add())/*9.52*/ {_display_(Seq[Any](format.raw/*9.54*/("""
    <dl>
      <input input type="text" name="label" required placeholder="Label of the device" class="inputL" />
      <input input type="url" name="url" placeholder="Publically reachable URL (optional)" class="inputL" />
      <input type="submit" value="Register" class="buttonLG"/>
    </dl>
  """)))})),format.raw/*15.4*/("""
  
  <br />
  """),_display_(Seq[Any](/*18.4*/vendpoint/*18.13*/.list/*18.18*/{_display_(Seq[Any](format.raw/*18.19*/("""<h1>Your end points</h1>""")))}/*18.44*/(endPoints))),format.raw/*18.55*/("""
  <br />
  """),_display_(Seq[Any](/*20.4*/vresource/*20.13*/.listStreams/*20.25*/{_display_(Seq[Any](format.raw/*20.26*/("""<h1>Your resources</h1>""")))}/*20.50*/(Resource.getByUser(CtrlUser.getUser())))),format.raw/*20.90*/("""
  <br />
  """),_display_(Seq[Any](/*22.4*/vlabel/*22.10*/.list/*22.15*/{_display_(Seq[Any](format.raw/*22.16*/("""<h1>Your labels</h1>""")))}/*22.37*/(User.getLabelsByUser(CtrlUser.getUser())))),format.raw/*22.79*/("""
  <br /> <br /> <br />
""")))})),format.raw/*24.2*/("""
"""))}
    }
    
    def render(endPoints:List[EndPoint]): play.api.templates.Html = apply(endPoints)
    
    def f:((List[EndPoint]) => play.api.templates.Html) = (endPoints) => apply(endPoints)
    
    def ref: this.type = this

}
                /*
                    -- GENERATED --
                    DATE: Mon Feb 11 23:20:20 CET 2013
                    SOURCE: /Users/ljm/code/SicsthSense/cloud-altmanage/app/views/configuration.scala.html
                    HASH: b3e1a1049b7a996f1a8a3a7d6448e26ecc1e5eaf
                    MATRIX: 739->1|859->28|887->47|923->49|956->74|995->76|1069->116|1083->122|1132->163|1171->165|1502->465|1553->481|1571->490|1585->495|1624->496|1668->521|1701->532|1749->545|1767->554|1788->566|1827->567|1870->591|1932->631|1980->644|1995->650|2009->655|2048->656|2088->677|2152->719|2208->744
                    LINES: 26->1|30->1|32->4|33->5|33->5|33->5|37->9|37->9|37->9|37->9|43->15|46->18|46->18|46->18|46->18|46->18|46->18|48->20|48->20|48->20|48->20|48->20|48->20|50->22|50->22|50->22|50->22|50->22|50->22|52->24
                    -- GENERATED --
                */
            