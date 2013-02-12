
package views.html.vendpoint

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
object followIcon extends BaseScalaTemplate[play.api.templates.Html,Format[play.api.templates.Html]](play.api.templates.HtmlFormat) with play.api.templates.Template2[EndPoint,String,play.api.templates.Html] {

    /**/
    def apply/*1.2*/(endPoint: EndPoint, prefix: String):play.api.templates.Html = {
        _display_ {

Seq[Any](format.raw/*1.38*/("""

"""),_display_(Seq[Any](/*3.2*/if(!CtrlUser.getUser().followsEndPoint(endPoint))/*3.51*/ {_display_(Seq[Any](format.raw/*3.53*/("""
  <a class="icon-star-empty follow_endpoint" parent_id=""""),_display_(Seq[Any](/*4.58*/endPoint/*4.66*/.id)),format.raw/*4.69*/("""" id="followep"""),_display_(Seq[Any](/*4.84*/prefix)),_display_(Seq[Any](/*4.91*/endPoint/*4.99*/.id)),format.raw/*4.102*/("""" href="#" title="Follow" inactive_title="Stop following"></a>
""")))}/*5.3*/else/*5.8*/{_display_(Seq[Any](format.raw/*5.9*/("""
  <a class="icon-star unfollow_endpoint" parent_id=""""),_display_(Seq[Any](/*6.54*/endPoint/*6.62*/.id)),format.raw/*6.65*/("""" id="unfollowep"""),_display_(Seq[Any](/*6.82*/prefix)),_display_(Seq[Any](/*6.89*/endPoint/*6.97*/.id)),format.raw/*6.100*/("""" href="#" title="Stop following" inactive_title="Followg"></a>
""")))})),format.raw/*7.2*/("""

"""))}
    }
    
    def render(endPoint:EndPoint,prefix:String): play.api.templates.Html = apply(endPoint,prefix)
    
    def f:((EndPoint,String) => play.api.templates.Html) = (endPoint,prefix) => apply(endPoint,prefix)
    
    def ref: this.type = this

}
                /*
                    -- GENERATED --
                    DATE: Mon Feb 11 23:20:21 CET 2013
                    SOURCE: /Users/ljm/code/SicsthSense/cloud-altmanage/app/views/vendpoint/followIcon.scala.html
                    HASH: 17a1ae007a976df35bfc7075e34a4e077c2044c9
                    MATRIX: 747->1|860->37|897->40|954->89|993->91|1086->149|1102->157|1126->160|1176->175|1212->182|1228->190|1253->193|1334->258|1345->263|1382->264|1471->318|1487->326|1511->329|1563->346|1599->353|1615->361|1640->364|1735->429
                    LINES: 26->1|29->1|31->3|31->3|31->3|32->4|32->4|32->4|32->4|32->4|32->4|32->4|33->5|33->5|33->5|34->6|34->6|34->6|34->6|34->6|34->6|34->6|35->7
                    -- GENERATED --
                */
            