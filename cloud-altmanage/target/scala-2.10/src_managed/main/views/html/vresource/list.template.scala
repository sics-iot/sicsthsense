
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
object list extends BaseScalaTemplate[play.api.templates.Html,Format[play.api.templates.Html]](play.api.templates.HtmlFormat) with play.api.templates.Template3[Html,List[Resource],Boolean,play.api.templates.Html] {

    /**/
    def apply/*1.2*/(header: Html)(resources: List[Resource], owned: Boolean):play.api.templates.Html = {
        _display_ {import helper._


Seq[Any](format.raw/*1.59*/("""

"""),_display_(Seq[Any](/*4.2*/if(resources.length > 0)/*4.26*/ {_display_(Seq[Any](format.raw/*4.28*/("""  
  """),_display_(Seq[Any](/*5.4*/header)),format.raw/*5.10*/("""
  <ul class="resource_list">
    """),_display_(Seq[Any](/*7.6*/for(resource <- resources) yield /*7.32*/ {_display_(Seq[Any](format.raw/*7.34*/("""
        <li>
          <a href=""""),_display_(Seq[Any](/*9.21*/routes/*9.27*/.CtrlResource.get(resource.id))),format.raw/*9.57*/("""">"""),_display_(Seq[Any](/*9.60*/resource/*9.68*/.path)),format.raw/*9.73*/("""</a> 
          """),_display_(Seq[Any](/*10.12*/resourceToolbar(resource, owned, resource.isShare(CtrlUser.getUser()), resource.isPublicAccess()))),format.raw/*10.109*/("""
        </li>
    """)))})),format.raw/*12.6*/("""
  </ul>
""")))})),format.raw/*14.2*/("""

"""))}
    }
    
    def render(header:Html,resources:List[Resource],owned:Boolean): play.api.templates.Html = apply(header)(resources,owned)
    
    def f:((Html) => (List[Resource],Boolean) => play.api.templates.Html) = (header) => (resources,owned) => apply(header)(resources,owned)
    
    def ref: this.type = this

}
                /*
                    -- GENERATED --
                    DATE: Mon Feb 11 23:20:21 CET 2013
                    SOURCE: /Users/ljm/code/SicsthSense/cloud-altmanage/app/views/vresource/list.scala.html
                    HASH: a343c0b431cbbd940e93b0d43b809e3391400904
                    MATRIX: 753->1|903->58|940->78|972->102|1011->104|1051->110|1078->116|1147->151|1188->177|1227->179|1296->213|1310->219|1361->249|1399->252|1415->260|1441->265|1494->282|1614->379|1665->399|1706->409
                    LINES: 26->1|30->1|32->4|32->4|32->4|33->5|33->5|35->7|35->7|35->7|37->9|37->9|37->9|37->9|37->9|37->9|38->10|38->10|40->12|42->14
                    -- GENERATED --
                */
            