
package views.html.vresource

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
object followIcon extends BaseScalaTemplate[play.api.templates.Html,Format[play.api.templates.Html]](play.api.templates.HtmlFormat) with play.api.templates.Template2[Resource,String,play.api.templates.Html] {

    /**/
    def apply/*1.2*/(resource: Resource, prefix: String):play.api.templates.Html = {
        _display_ {

Seq[Any](format.raw/*1.38*/("""
 
"""),_display_(Seq[Any](/*3.2*/if(!CtrlUser.getUser().followsResource(resource))/*3.51*/ {_display_(Seq[Any](format.raw/*3.53*/("""
  <a class="icon-btn icon-star-empty follow_resource" parent_id=""""),_display_(Seq[Any](/*4.67*/resource/*4.75*/.id)),format.raw/*4.78*/("""" id="followr"""),_display_(Seq[Any](/*4.92*/prefix)),_display_(Seq[Any](/*4.99*/resource/*4.107*/.id)),format.raw/*4.110*/("""" href="#" rel="tooltip" title="Follow" inactive_title="Stop following"></a>
""")))}/*5.3*/else/*5.8*/{_display_(Seq[Any](format.raw/*5.9*/("""
  <a class="icon-btn icon-star unfollow_resource" parent_id=""""),_display_(Seq[Any](/*6.63*/resource/*6.71*/.id)),format.raw/*6.74*/("""" id="unfollowr"""),_display_(Seq[Any](/*6.90*/prefix)),_display_(Seq[Any](/*6.97*/resource/*6.105*/.id)),format.raw/*6.108*/("""" href="#" rel="tooltip" title="Stop following" inactive_title="Follow"></a>
""")))})),format.raw/*7.2*/("""

"""))}
    }
    
    def render(resource:Resource,prefix:String): play.api.templates.Html = apply(resource,prefix)
    
    def f:((Resource,String) => play.api.templates.Html) = (resource,prefix) => apply(resource,prefix)
    
    def ref: this.type = this

}
                /*
                    -- GENERATED --
                    DATE: Mon Feb 11 23:20:21 CET 2013
                    SOURCE: /Users/ljm/code/SicsthSense/cloud-altmanage/app/views/vresource/followIcon.scala.html
                    HASH: 0339c284437f93b6138dcd8341ad133f7ed85515
                    MATRIX: 747->1|860->37|898->41|955->90|994->92|1096->159|1112->167|1136->170|1185->184|1221->191|1238->199|1263->202|1358->281|1369->286|1406->287|1504->350|1520->358|1544->361|1595->377|1631->384|1648->392|1673->395|1781->473
                    LINES: 26->1|29->1|31->3|31->3|31->3|32->4|32->4|32->4|32->4|32->4|32->4|32->4|33->5|33->5|33->5|34->6|34->6|34->6|34->6|34->6|34->6|34->6|35->7
                    -- GENERATED --
                */
            