
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
object list extends BaseScalaTemplate[play.api.templates.Html,Format[play.api.templates.Html]](play.api.templates.HtmlFormat) with play.api.templates.Template2[Html,List[EndPoint],play.api.templates.Html] {

    /**/
    def apply/*1.2*/(header: Html)(endPoints: List[EndPoint]):play.api.templates.Html = {
        _display_ {

Seq[Any](format.raw/*1.43*/("""
    
"""),_display_(Seq[Any](/*3.2*/if(endPoints.length > 0)/*3.26*/ {_display_(Seq[Any](format.raw/*3.28*/("""

  """),_display_(Seq[Any](/*5.4*/header)),format.raw/*5.10*/("""
<ul class="unstyled endpoint_list">
  """),_display_(Seq[Any](/*7.4*/for(endPoint <- endPoints) yield /*7.30*/ {_display_(Seq[Any](format.raw/*7.32*/("""
    <li class="endpoint" id=""""),_display_(Seq[Any](/*8.31*/endPoint/*8.39*/.id)),format.raw/*8.42*/("""">
      <a href="#" class="hide_resources icon-chevron-down"></a>
      <a href=""""),_display_(Seq[Any](/*10.17*/routes/*10.23*/.CtrlEndPoint.get(endPoint.id))),format.raw/*10.53*/("""">"""),_display_(Seq[Any](/*10.56*/endPoint/*10.64*/.label)),format.raw/*10.70*/("""</a>
      """),_display_(Seq[Any](/*11.8*/vendpoint/*11.17*/.followIcon(endPoint, "eplist"))),format.raw/*11.48*/(""" 
      <ul class="resource_list">
      """),_display_(Seq[Any](/*13.8*/Resource/*13.16*/.getByEndPoint(endPoint).map/*13.44*/ { resource =>_display_(Seq[Any](format.raw/*13.58*/("""
<!--            <li> -->
<!--            <a href=""""),_display_(Seq[Any](/*15.27*/routes/*15.33*/.CtrlResource.get(resource.id))),format.raw/*15.63*/("""">"""),_display_(Seq[Any](/*15.66*/resource/*15.74*/.path)),format.raw/*15.79*/("""</a> -->
<!--             """),_display_(Seq[Any](/*16.19*/vresource/*16.28*/.followIcon(resource, "eplist"))),format.raw/*16.59*/(""" -->
<!--             """),_display_(Seq[Any](/*17.19*/if(resource.lastUpdated != 0)/*17.48*/ {_display_(Seq[Any](format.raw/*17.50*/(""" -->
<!--             <span class="sideinfo">[updated """),_display_(Seq[Any](/*18.51*/Utils/*18.56*/.timeStr((Utils.currentTime()-resource.lastUpdated)))),format.raw/*18.108*/(""" ago]</span> -->
<!--             """)))})),format.raw/*19.19*/(""" -->
<!--             """),_display_(Seq[Any](/*20.19*/if(resource.pollingPeriod != 0)/*20.50*/ {_display_(Seq[Any](format.raw/*20.52*/(""" -->
<!--             <span class="sideinfo">[polled every """),_display_(Seq[Any](/*21.56*/Utils/*21.61*/.timeStr(resource.pollingPeriod))),format.raw/*21.93*/("""]</span> -->
<!--             """)))})),format.raw/*22.19*/(""" -->
<!--            </li> -->
        <li>
          <a href=""""),_display_(Seq[Any](/*25.21*/routes/*25.27*/.CtrlResource.get(resource.id))),format.raw/*25.57*/("""">"""),_display_(Seq[Any](/*25.60*/resource/*25.68*/.path)),format.raw/*25.73*/("""</a> 
          """),_display_(Seq[Any](/*26.12*/vresource/*26.21*/.resourceToolbar(resource, resource.isOwnedBy(CtrlUser.getUser()), resource.isShare(CtrlUser.getUser()), resource.isPublicAccess()))),format.raw/*26.152*/("""
        </li>
       """)))})),format.raw/*28.9*/("""
      </ul>
   </li>
  """)))})),format.raw/*31.4*/("""
</ul>

""")))})),format.raw/*34.2*/("""
"""))}
    }
    
    def render(header:Html,endPoints:List[EndPoint]): play.api.templates.Html = apply(header)(endPoints)
    
    def f:((Html) => (List[EndPoint]) => play.api.templates.Html) = (header) => (endPoints) => apply(header)(endPoints)
    
    def ref: this.type = this

}
                /*
                    -- GENERATED --
                    DATE: Tue Feb 12 09:39:12 CET 2013
                    SOURCE: /Users/ljm/code/SicsthSense/cloud-altmanage/app/views/vendpoint/list.scala.html
                    HASH: 689aa772d4d743a67701b47d10e4360dfd2cc858
                    MATRIX: 745->1|863->42|904->49|936->73|975->75|1014->80|1041->86|1115->126|1156->152|1195->154|1261->185|1277->193|1301->196|1420->279|1435->285|1487->315|1526->318|1543->326|1571->332|1618->344|1636->353|1689->384|1766->426|1783->434|1820->462|1872->476|1960->528|1975->534|2027->564|2066->567|2083->575|2110->580|2173->607|2191->616|2244->647|2303->670|2341->699|2381->701|2472->756|2486->761|2561->813|2628->848|2687->871|2727->902|2767->904|2863->964|2877->969|2931->1001|2994->1032|3094->1096|3109->1102|3161->1132|3200->1135|3217->1143|3244->1148|3297->1165|3315->1174|3469->1305|3523->1328|3579->1353|3619->1362
                    LINES: 26->1|29->1|31->3|31->3|31->3|33->5|33->5|35->7|35->7|35->7|36->8|36->8|36->8|38->10|38->10|38->10|38->10|38->10|38->10|39->11|39->11|39->11|41->13|41->13|41->13|41->13|43->15|43->15|43->15|43->15|43->15|43->15|44->16|44->16|44->16|45->17|45->17|45->17|46->18|46->18|46->18|47->19|48->20|48->20|48->20|49->21|49->21|49->21|50->22|53->25|53->25|53->25|53->25|53->25|53->25|54->26|54->26|54->26|56->28|59->31|62->34
                    -- GENERATED --
                */
            