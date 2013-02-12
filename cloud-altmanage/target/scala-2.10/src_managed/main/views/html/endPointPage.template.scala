
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
object endPointPage extends BaseScalaTemplate[play.api.templates.Html,Format[play.api.templates.Html]](play.api.templates.HtmlFormat) with play.api.templates.Template3[EndPoint,Form[EndPoint],Boolean,play.api.templates.Html] {

    /**/
    def apply/*1.2*/(endPoint: EndPoint, epForm: Form[EndPoint], owned: Boolean):play.api.templates.Html = {
        _display_ {import helper._


Seq[Any](format.raw/*1.62*/("""

"""),format.raw/*4.1*/("""
"""),_display_(Seq[Any](/*5.2*/layout(endPoint.label, session)/*5.33*/ {_display_(Seq[Any](format.raw/*5.35*/("""
  
  <h1>End Point</h1>
  
  <dl>
  <dt>Full path</dt>
  <dd>"""),_display_(Seq[Any](/*11.8*/endPoint/*11.16*/.getUser().userName)),format.raw/*11.35*/("""/"""),_display_(Seq[Any](/*11.37*/endPoint/*11.45*/.label)),format.raw/*11.51*/("""  """),_display_(Seq[Any](/*11.54*/vendpoint/*11.63*/.followIcon(endPoint, "ep"))),format.raw/*11.90*/("""</dd>
  </dl>
  """),_display_(Seq[Any](/*13.4*/if(owned)/*13.13*/ {_display_(Seq[Any](format.raw/*13.15*/("""
	  """),_display_(Seq[Any](/*14.5*/if(epForm != null)/*14.23*/ {_display_(Seq[Any](format.raw/*14.25*/("""
	    <span class="logged"><a href=""""),_display_(Seq[Any](/*15.37*/routes/*15.43*/.CtrlEndPoint.get(endPoint.id))),format.raw/*15.73*/("""">[Cancel]</a></span>
	  """)))}/*16.6*/else/*16.11*/{_display_(Seq[Any](format.raw/*16.12*/("""
	    <span class="logged"><a href=""""),_display_(Seq[Any](/*17.37*/routes/*17.43*/.CtrlEndPoint.edit(endPoint.id))),format.raw/*17.74*/("""">[Edit]</a></span>
	  """)))})),format.raw/*18.5*/("""
	""")))})),format.raw/*19.3*/("""
  """),_display_(Seq[Any](/*20.4*/forms/*20.9*/.form(routes.CtrlEndPoint.submit(endPoint.id))/*20.55*/ {_display_(Seq[Any](format.raw/*20.57*/("""
      <dt>User</dt>
      <dd>"""),_display_(Seq[Any](/*22.12*/endPoint/*22.20*/.getUser().userName)),format.raw/*22.39*/("""</dd>
      """),_display_(Seq[Any](/*23.8*/forms/*23.13*/.input(epForm!=null, "text", "label", "Label", endPoint.label))),format.raw/*23.75*/("""
      """),_display_(Seq[Any](/*24.8*/forms/*24.13*/.input(epForm!=null, "url", "url", "URL", endPoint.url))),format.raw/*24.68*/("""
      """),_display_(Seq[Any](/*25.8*/forms/*25.13*/.input(epForm!=null, "text", "description", "Description", endPoint.description))),format.raw/*25.93*/("""
      """),_display_(Seq[Any](/*26.8*/forms/*26.13*/.input(epForm!=null, "text", "location", "Location", endPoint.location))),format.raw/*26.84*/("""
      """),_display_(Seq[Any](/*27.8*/if(epForm!=null)/*27.24*/ {_display_(Seq[Any](format.raw/*27.26*/(""" """),_display_(Seq[Any](/*27.28*/forms/*27.33*/.submit(epForm!=null, "Update"))),format.raw/*27.64*/(""" """)))})),format.raw/*27.66*/("""
  """)))})),format.raw/*28.4*/("""
	<br /> <br /> 
  """),_display_(Seq[Any](/*30.4*/if(owned)/*30.13*/ {_display_(Seq[Any](format.raw/*30.15*/("""
      <dl>
      """),_display_(Seq[Any](/*32.8*/forms/*32.13*/.form(routes.CtrlEndPoint.addResource(endPoint.id))/*32.64*/ {_display_(Seq[Any](format.raw/*32.66*/("""
        <dt class="action">Add resource</dt>
        <dd>  
          <input input type="text" name="path" required placeholder="path" class="input" size="8"/>
          <input type="submit" value="Ok" class="buttonG"/>
        </dd>
      """)))})),format.raw/*38.8*/("""
      """),_display_(Seq[Any](/*39.8*/if(endPoint.hasUrl())/*39.29*/ {_display_(Seq[Any](format.raw/*39.31*/("""
        <dt class="action">Discover resources</dt>
        <dd><a href=""""),_display_(Seq[Any](/*41.23*/routes/*41.29*/.CtrlEndPoint.discover(endPoint.id))),format.raw/*41.64*/("""" class="update">[discover]</a></dd>
      """)))})),format.raw/*42.8*/("""
      <dt class="action">Delete end point</dt>
      <dd><a href=""""),_display_(Seq[Any](/*44.21*/routes/*44.27*/.CtrlEndPoint.delete(endPoint.id))),format.raw/*44.60*/("""" class="delete">[delete]</a></dd>
      </dl>
	""")))})),format.raw/*46.3*/("""
	<br /> <br /> 

  """),_display_(Seq[Any](/*49.4*/vresource/*49.13*/.list/*49.18*/{_display_(Seq[Any](format.raw/*49.19*/("""<h1>Resource manager</h1>""")))}/*49.45*/(Resource.getByEndPoint(endPoint), owned))),format.raw/*49.86*/("""
  
  """),_display_(Seq[Any](/*51.4*/vresource/*51.13*/.listStreams/*51.25*/{_display_(Seq[Any](format.raw/*51.26*/("""<h1>Resources</h1>""")))}/*51.45*/(Resource.getByEndPoint(endPoint)))),format.raw/*51.79*/("""

""")))})),format.raw/*53.2*/("""
"""))}
    }
    
    def render(endPoint:EndPoint,epForm:Form[EndPoint],owned:Boolean): play.api.templates.Html = apply(endPoint,epForm,owned)
    
    def f:((EndPoint,Form[EndPoint],Boolean) => play.api.templates.Html) = (endPoint,epForm,owned) => apply(endPoint,epForm,owned)
    
    def ref: this.type = this

}
                /*
                    -- GENERATED --
                    DATE: Mon Feb 11 23:20:20 CET 2013
                    SOURCE: /Users/ljm/code/SicsthSense/cloud-altmanage/app/views/endPointPage.scala.html
                    HASH: 082556f9faf0b3bdfae5d3a22a9b6ced9432fc13
                    MATRIX: 755->1|908->61|936->80|972->82|1011->113|1050->115|1148->178|1165->186|1206->205|1244->207|1261->215|1289->221|1328->224|1346->233|1395->260|1447->277|1465->286|1505->288|1545->293|1572->311|1612->313|1685->350|1700->356|1752->386|1796->413|1809->418|1848->419|1921->456|1936->462|1989->493|2044->517|2078->520|2117->524|2130->529|2185->575|2225->577|2293->609|2310->617|2351->636|2399->649|2413->654|2497->716|2540->724|2554->729|2631->784|2674->792|2688->797|2790->877|2833->885|2847->890|2940->961|2983->969|3008->985|3048->987|3086->989|3100->994|3153->1025|3187->1027|3222->1031|3277->1051|3295->1060|3335->1062|3389->1081|3403->1086|3463->1137|3503->1139|3776->1381|3819->1389|3849->1410|3889->1412|3999->1486|4014->1492|4071->1527|4146->1571|4250->1639|4265->1645|4320->1678|4400->1727|4456->1748|4474->1757|4488->1762|4527->1763|4572->1789|4635->1830|4677->1837|4695->1846|4716->1858|4755->1859|4793->1878|4849->1912|4883->1915
                    LINES: 26->1|30->1|32->4|33->5|33->5|33->5|39->11|39->11|39->11|39->11|39->11|39->11|39->11|39->11|39->11|41->13|41->13|41->13|42->14|42->14|42->14|43->15|43->15|43->15|44->16|44->16|44->16|45->17|45->17|45->17|46->18|47->19|48->20|48->20|48->20|48->20|50->22|50->22|50->22|51->23|51->23|51->23|52->24|52->24|52->24|53->25|53->25|53->25|54->26|54->26|54->26|55->27|55->27|55->27|55->27|55->27|55->27|55->27|56->28|58->30|58->30|58->30|60->32|60->32|60->32|60->32|66->38|67->39|67->39|67->39|69->41|69->41|69->41|70->42|72->44|72->44|72->44|74->46|77->49|77->49|77->49|77->49|77->49|77->49|79->51|79->51|79->51|79->51|79->51|79->51|81->53
                    -- GENERATED --
                */
            