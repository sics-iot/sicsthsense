
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
object listStreams extends BaseScalaTemplate[play.api.templates.Html,Format[play.api.templates.Html]](play.api.templates.HtmlFormat) with play.api.templates.Template2[Html,List[Resource],play.api.templates.Html] {

    /**/
    def apply/*1.2*/(header: Html)(resources: List[Resource]):play.api.templates.Html = {
        _display_ {

Seq[Any](format.raw/*1.43*/("""

"""),_display_(Seq[Any](/*3.2*/if(resources.length > 0)/*3.26*/ {_display_(Seq[Any](format.raw/*3.28*/("""
  
  """),_display_(Seq[Any](/*5.4*/header)),format.raw/*5.10*/("""
  
  <ul>
    """),_display_(Seq[Any](/*8.6*/for(resource <- resources) yield /*8.32*/ {_display_(Seq[Any](format.raw/*8.34*/("""
      <li> 
      <a href=""""),_display_(Seq[Any](/*10.17*/routes/*10.23*/.CtrlResource.get(resource.id))),format.raw/*10.53*/("""">"""),_display_(Seq[Any](/*10.56*/resource/*10.64*/.getUser().userName)),format.raw/*10.83*/("""/"""),_display_(Seq[Any](/*10.85*/resource/*10.93*/.getEndPoint().label)),_display_(Seq[Any](/*10.114*/resource/*10.122*/.path)),format.raw/*10.127*/("""</a>
      """),_display_(Seq[Any](/*11.8*/vresource/*11.17*/.followIcon(resource, "slist"))),format.raw/*11.47*/("""
      """),_display_(Seq[Any](/*12.8*/if(resource.pollingPeriod != 0)/*12.39*/ {_display_(Seq[Any](format.raw/*12.41*/("""
        [polled every """),_display_(Seq[Any](/*13.24*/{resource.pollingPeriod/60})),format.raw/*13.51*/(""" min]
      """)))})),format.raw/*14.8*/("""
      """),_display_(Seq[Any](/*15.8*/vresource/*15.17*/.include(resource))),format.raw/*15.35*/("""
      """),_display_(Seq[Any](/*16.8*/vresource/*16.17*/.includeStream(resource))),format.raw/*16.41*/("""
      </li>
    """)))})),format.raw/*18.6*/("""
  </ul>
""")))})),format.raw/*20.2*/("""
"""))}
    }
    
    def render(header:Html,resources:List[Resource]): play.api.templates.Html = apply(header)(resources)
    
    def f:((Html) => (List[Resource]) => play.api.templates.Html) = (header) => (resources) => apply(header)(resources)
    
    def ref: this.type = this

}
                /*
                    -- GENERATED --
                    DATE: Mon Feb 11 23:20:21 CET 2013
                    SOURCE: /Users/ljm/code/SicsthSense/cloud-altmanage/app/views/vresource/listStreams.scala.html
                    HASH: 44727907292d32ee89301663838ea87cb69e747c
                    MATRIX: 752->1|870->42|907->45|939->69|978->71|1019->78|1046->84|1096->100|1137->126|1176->128|1241->157|1256->163|1308->193|1347->196|1364->204|1405->223|1443->225|1460->233|1512->254|1530->262|1558->267|1605->279|1623->288|1675->318|1718->326|1758->357|1798->359|1858->383|1907->410|1951->423|1994->431|2012->440|2052->458|2095->466|2113->475|2159->499|2208->517|2249->527
                    LINES: 26->1|29->1|31->3|31->3|31->3|33->5|33->5|36->8|36->8|36->8|38->10|38->10|38->10|38->10|38->10|38->10|38->10|38->10|38->10|38->10|38->10|39->11|39->11|39->11|40->12|40->12|40->12|41->13|41->13|42->14|43->15|43->15|43->15|44->16|44->16|44->16|46->18|48->20
                    -- GENERATED --
                */
            