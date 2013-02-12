
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
object resourcePage extends BaseScalaTemplate[play.api.templates.Html,Format[play.api.templates.Html]](play.api.templates.HtmlFormat) with play.api.templates.Template2[Resource,Boolean,play.api.templates.Html] {

    /**/
    def apply/*1.2*/(resource: Resource, owned: Boolean):play.api.templates.Html = {
        _display_ {import helper._


Seq[Any](format.raw/*1.38*/("""

"""),format.raw/*4.1*/("""
"""),_display_(Seq[Any](/*5.2*/layout("Resource Details: @resource.path", session)/*5.53*/ {_display_(Seq[Any](format.raw/*5.55*/("""

  """),_display_(Seq[Any](/*7.4*/displayResource(resource, owned, resource.getEndPoint(), resource.getUser()))),format.raw/*7.80*/("""
 
  """),_display_(Seq[Any](/*9.4*/vresource/*9.13*/.include(resource))),format.raw/*9.31*/("""

  
"""),_display_(Seq[Any](/*12.2*/if(owned)/*12.11*/ {_display_(Seq[Any](format.raw/*12.13*/("""
<dl>
  """),_display_(Seq[Any](/*14.4*/if(resource.pollingPeriod != 0)/*14.35*/ {_display_(Seq[Any](format.raw/*14.37*/("""
    <dt class="action">Stop polling</dt>
    <dd><a href=""""),_display_(Seq[Any](/*16.19*/routes/*16.25*/.CtrlResource.setPeriod(resource.id, 0))),format.raw/*16.64*/("""" class="delete">[stop]</a></dd>
  """)))})),format.raw/*17.4*/("""
  """),_display_(Seq[Any](/*18.4*/if(resource.hasData())/*18.26*/ {_display_(Seq[Any](format.raw/*18.28*/("""
    <dt class="action">Clear stream</dt> 
    <dd><a href=""""),_display_(Seq[Any](/*20.19*/routes/*20.25*/.CtrlResource.clearStream(resource.id))),format.raw/*20.63*/("""" class="delete">[clear]</a></dd>
  """)))})),format.raw/*21.4*/("""
    <dt class="action">Delete resource</dt>
    <dd><a href=""""),_display_(Seq[Any](/*23.19*/routes/*23.25*/.CtrlResource.delete(resource.id))),format.raw/*23.58*/("""" class="delete">[delete]</a></dd>
  """)))})),format.raw/*24.4*/("""
  </dl>
  """),_display_(Seq[Any](/*26.4*/vresource/*26.13*/.includeStream(resource))),format.raw/*26.37*/(""" 
""")))})),format.raw/*27.2*/("""
"""))}
    }
    
    def render(resource:Resource,owned:Boolean): play.api.templates.Html = apply(resource,owned)
    
    def f:((Resource,Boolean) => play.api.templates.Html) = (resource,owned) => apply(resource,owned)
    
    def ref: this.type = this

}
                /*
                    -- GENERATED --
                    DATE: Mon Feb 11 23:20:21 CET 2013
                    SOURCE: /Users/ljm/code/SicsthSense/cloud-altmanage/app/views/resourcePage.scala.html
                    HASH: 534dad5d8e83e85c57bd4a6e9d97c8ea4617b75f
                    MATRIX: 740->1|869->37|897->56|933->58|992->109|1031->111|1070->116|1167->192|1207->198|1224->207|1263->225|1304->231|1322->240|1362->242|1406->251|1446->282|1486->284|1582->344|1597->350|1658->389|1725->425|1764->429|1795->451|1835->453|1932->514|1947->520|2007->558|2075->595|2174->658|2189->664|2244->697|2313->735|2360->747|2378->756|2424->780|2458->783
                    LINES: 26->1|30->1|32->4|33->5|33->5|33->5|35->7|35->7|37->9|37->9|37->9|40->12|40->12|40->12|42->14|42->14|42->14|44->16|44->16|44->16|45->17|46->18|46->18|46->18|48->20|48->20|48->20|49->21|51->23|51->23|51->23|52->24|54->26|54->26|54->26|55->27
                    -- GENERATED --
                */
            