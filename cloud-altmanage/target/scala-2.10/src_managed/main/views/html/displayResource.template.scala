
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
object displayResource extends BaseScalaTemplate[play.api.templates.Html,Format[play.api.templates.Html]](play.api.templates.HtmlFormat) with play.api.templates.Template4[Resource,Boolean,EndPoint,User,play.api.templates.Html] {

    /**/
    def apply/*1.2*/(resource: Resource, owned: Boolean, endPoint: EndPoint, user: User):play.api.templates.Html = {
        _display_ {import helper._


Seq[Any](format.raw/*1.70*/(""" 

"""),format.raw/*4.1*/("""
  <h1>Resource</h1>

  <dl>
  <dt>Full path</dt>
  <dd>"""),_display_(Seq[Any](/*9.8*/user/*9.12*/.userName)),format.raw/*9.21*/("""/"""),_display_(Seq[Any](/*9.23*/endPoint/*9.31*/.label)),_display_(Seq[Any](/*9.38*/resource/*9.46*/.path)),format.raw/*9.51*/("""  """),_display_(Seq[Any](/*9.54*/vresource/*9.63*/.followIcon(resource, "r"))),format.raw/*9.89*/("""</dd>
  <dt>User</dt>
  <dd>"""),_display_(Seq[Any](/*11.8*/user/*11.12*/.userName)),format.raw/*11.21*/("""</dd>
    <dl>
    <dt>End point</dt>
    <dd><a href=""""),_display_(Seq[Any](/*14.19*/routes/*14.25*/.CtrlEndPoint.get(endPoint.id))),format.raw/*14.55*/("""">"""),_display_(Seq[Any](/*14.58*/endPoint/*14.66*/.label)),format.raw/*14.72*/("""</a></dd>
    <dt>Path</dt>
    <dd>"""),_display_(Seq[Any](/*16.10*/resource/*16.18*/.path)),format.raw/*16.23*/("""</dd>
	<dt>Label</dt>
    <dd>"""),_display_(Seq[Any](/*18.10*/resource/*18.18*/.label)),format.raw/*18.24*/("""</dd>
    """),_display_(Seq[Any](/*19.6*/if(!resource.inputParser.equals(""))/*19.42*/ {_display_(Seq[Any](format.raw/*19.44*/("""
    <dt>Input parser</dt>
    <dd>"""),_display_(Seq[Any](/*21.10*/resource/*21.18*/.inputParser)),format.raw/*21.30*/("""</dd>
    """)))})),format.raw/*22.6*/("""
    """),_display_(Seq[Any](/*23.6*/if(resource.pollingPeriod > 0)/*23.36*/ {_display_(Seq[Any](format.raw/*23.38*/("""
    <dt>Polling interval</dt>
  <dd>"""),_display_(Seq[Any](/*25.8*/{resource.pollingPeriod/60})),format.raw/*25.35*/(""" minutes</dd>
  """)))})),format.raw/*26.4*/("""
  """),_display_(Seq[Any](/*27.4*/if(resource.lastPolled > 0)/*27.31*/ {_display_(Seq[Any](format.raw/*27.33*/("""
  <dt>Last polled</dt>
  <dd>"""),_display_(Seq[Any](/*29.8*/{(controllers.Utils.currentTime()-resource.lastPolled)/60})),format.raw/*29.66*/(""" minutes ago</dd>
  """)))})),format.raw/*30.4*/("""
  """),_display_(Seq[Any](/*31.4*/if(resource.lastUpdated > 0)/*31.32*/ {_display_(Seq[Any](format.raw/*31.34*/("""
  <dt>Last updated</dt>
  <dd>"""),_display_(Seq[Any](/*33.8*/{(controllers.Utils.currentTime()-resource.lastUpdated)/60})),format.raw/*33.67*/(""" minutes ago</dd>
  """)))})),format.raw/*34.4*/("""
  """),_display_(Seq[Any](/*35.4*/if(owned)/*35.13*/{_display_(Seq[Any](format.raw/*35.14*/("""
  """),_display_(Seq[Any](/*36.4*/if(endPoint.hasUrl())/*36.25*/ {_display_(Seq[Any](format.raw/*36.27*/("""
	  """),_display_(Seq[Any](/*37.5*/form(routes.CtrlResource.setPeriod(resource.id,0))/*37.55*/ {_display_(Seq[Any](format.raw/*37.57*/("""
	    <dt class="action">Enter polling interval</dt>
        <dd>  
          <input input type="hidden" name="id" value=""""),_display_(Seq[Any](/*40.56*/resource/*40.64*/.id)),format.raw/*40.67*/("""" />
          <input input type="number" name="period" required placeholder="Period" class="input" size="4" style="text-align:right;"/>
          <input type="submit" value="Ok" class="buttonG"/>
        </dd>
      """)))})),format.raw/*44.8*/("""
  """)))})),format.raw/*45.4*/("""
  """),_display_(Seq[Any](/*46.4*/form(routes.CtrlResource.setLabelName(resource.id,""))/*46.58*/ {_display_(Seq[Any](format.raw/*46.60*/("""
	    <dt class="action">Set label</dt>
        <dd>  
          <input input type="hidden" name="id" value=""""),_display_(Seq[Any](/*49.56*/resource/*49.64*/.id)),format.raw/*49.67*/("""" />
          <input input type="string" name="label" required placeholder="Label" class="input" size="4" style="text-align:right;"/>
          <input type="submit" value="Ok" class="buttonG"/>
        </dd>
        """)))})),format.raw/*53.10*/("""
  """),_display_(Seq[Any](/*54.4*/form(routes.CtrlResource.setInputParser(resource.id,""))/*54.60*/ {_display_(Seq[Any](format.raw/*54.62*/("""
	    <dt class="action">Set input parser (regexp)</dt>
        <dd>  
          <input input type="hidden" name="id" value=""""),_display_(Seq[Any](/*57.56*/resource/*57.64*/.id)),format.raw/*57.67*/("""" />
          <input input type="string" name="parser" required placeholder="Parser" class="input" size="4" style="text-align:right;"/>
          <input type="submit" value="Ok" class="buttonG"/>
          (<a href="http://regexpal.com/">regexpal.com</a> is useful!)
        </dd>
        """)))})),format.raw/*62.10*/("""
        """)))})),format.raw/*63.10*/("""
  </dl>
"""))}
    }
    
    def render(resource:Resource,owned:Boolean,endPoint:EndPoint,user:User): play.api.templates.Html = apply(resource,owned,endPoint,user)
    
    def f:((Resource,Boolean,EndPoint,User) => play.api.templates.Html) = (resource,owned,endPoint,user) => apply(resource,owned,endPoint,user)
    
    def ref: this.type = this

}
                /*
                    -- GENERATED --
                    DATE: Mon Feb 11 23:20:20 CET 2013
                    SOURCE: /Users/ljm/code/SicsthSense/cloud-altmanage/app/views/displayResource.scala.html
                    HASH: dc20073fae8578bd60ecf2657b4d761415eb6942
                    MATRIX: 757->1|918->69|947->89|1038->146|1050->150|1080->159|1117->161|1133->169|1169->176|1185->184|1211->189|1249->192|1266->201|1313->227|1377->256|1390->260|1421->269|1513->325|1528->331|1580->361|1619->364|1636->372|1664->378|1737->415|1754->423|1781->428|1848->459|1865->467|1893->473|1939->484|1984->520|2024->522|2096->558|2113->566|2147->578|2189->589|2230->595|2269->625|2309->627|2382->665|2431->692|2479->709|2518->713|2554->740|2594->742|2660->773|2740->831|2792->852|2831->856|2868->884|2908->886|2975->918|3056->977|3108->998|3147->1002|3165->1011|3204->1012|3243->1016|3273->1037|3313->1039|3353->1044|3412->1094|3452->1096|3611->1219|3628->1227|3653->1230|3902->1448|3937->1452|3976->1456|4039->1510|4079->1512|4225->1622|4242->1630|4267->1633|4517->1851|4556->1855|4621->1911|4661->1913|4823->2039|4840->2047|4865->2050|5188->2341|5230->2351
                    LINES: 26->1|30->1|32->4|37->9|37->9|37->9|37->9|37->9|37->9|37->9|37->9|37->9|37->9|37->9|39->11|39->11|39->11|42->14|42->14|42->14|42->14|42->14|42->14|44->16|44->16|44->16|46->18|46->18|46->18|47->19|47->19|47->19|49->21|49->21|49->21|50->22|51->23|51->23|51->23|53->25|53->25|54->26|55->27|55->27|55->27|57->29|57->29|58->30|59->31|59->31|59->31|61->33|61->33|62->34|63->35|63->35|63->35|64->36|64->36|64->36|65->37|65->37|65->37|68->40|68->40|68->40|72->44|73->45|74->46|74->46|74->46|77->49|77->49|77->49|81->53|82->54|82->54|82->54|85->57|85->57|85->57|90->62|91->63
                    -- GENERATED --
                */
            