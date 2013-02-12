
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
object managePage extends BaseScalaTemplate[play.api.templates.Html,Format[play.api.templates.Html]](play.api.templates.HtmlFormat) with play.api.templates.Template1[List[EndPoint],play.api.templates.Html] {

    /**/
    def apply/*1.2*/(endPoints: List[EndPoint]):play.api.templates.Html = {
        _display_ {import helper._


Seq[Any](format.raw/*1.29*/("""

"""),format.raw/*4.1*/("""
"""),_display_(Seq[Any](/*5.2*/layout("Manage", session)/*5.27*/ {_display_(Seq[Any](format.raw/*5.29*/("""
 

  <br /> 

<div id="wrapper">
  <div id="tabContainer">
    <div id="tabs">
      <ul>
        <li id="tabHeader_1">Devices</li>
        <li id="tabHeader_2">Streams</li>
      </ul>
    </div>
    <div id="tabscontent">
      <div class="tabpage" id="tabpage_1">

	  <h1>Register new device</h1>
	 
		"""),_display_(Seq[Any](/*23.4*/helper/*23.10*/.form(action = routes.CtrlEndPoint.add())/*23.51*/ {_display_(Seq[Any](format.raw/*23.53*/("""
		<dl>
		  <input input type="text" name="label" required placeholder="Label of the device" class="inputL" />
		  <input input type="url" name="url" placeholder="Publically reachable URL (optional)" class="inputL" />
		  <input type="submit" value="Register" class="buttonLG"/>
		</dl>
		""")))})),format.raw/*29.4*/("""
  
		<br /> <br />
		"""),_display_(Seq[Any](/*32.4*/vendpoint/*32.13*/.list/*32.18*/{_display_(Seq[Any](format.raw/*32.19*/("""<h2>Devices</h2>""")))}/*32.36*/(endPoints))),format.raw/*32.47*/("""
		<br /> <br />
      </div>

      <div class="tabpage" id="tabpage_2">
	  <h1>Add new stream</h1>
	 
	   """),_display_(Seq[Any](/*39.6*/helper/*39.12*/.form(action = routes.CtrlEndPoint.add())/*39.53*/ {_display_(Seq[Any](format.raw/*39.55*/("""
		<dl>
		  <input input type="text" name="label" required placeholder="Label of the device" class="inputL" />
		  <input input type="url" name="url" placeholder="Publically reachable URL (optional)" class="inputL" />
		  <input type="submit" value="Register" class="buttonLG"/>
		</dl>
	  """)))})),format.raw/*45.5*/("""
		<br /> <br />
		"""),_display_(Seq[Any](/*47.4*/vresource/*47.13*/.listStreams/*47.25*/{_display_(Seq[Any](format.raw/*47.26*/("""<h2>Streams</h2>""")))}/*47.43*/(Resource.getByUser(CtrlUser.getUser())))),format.raw/*47.83*/("""
		<br /> <br />
      </div>
    </div>
  </div>
</div>

<script src=""""),_display_(Seq[Any](/*54.15*/routes/*54.21*/.Assets.at("javascripts/tabs.js"))),format.raw/*54.54*/("""" type="text/javascript"></script>
<script type="text/javascript">
  var _gaq = _gaq || [];
  _gaq.push(['_setAccount', 'UA-1332079-8']);
  _gaq.push(['_trackPageview']);
  (function() """),format.raw/*59.15*/("""{"""),format.raw/*59.16*/("""
    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
  """),format.raw/*63.3*/("""}"""),format.raw/*63.4*/(""")();

</script>
""")))})),format.raw/*66.2*/("""
  <br /> <br /> <br />


"""))}
    }
    
    def render(endPoints:List[EndPoint]): play.api.templates.Html = apply(endPoints)
    
    def f:((List[EndPoint]) => play.api.templates.Html) = (endPoints) => apply(endPoints)
    
    def ref: this.type = this

}
                /*
                    -- GENERATED --
                    DATE: Tue Feb 12 09:52:57 CET 2013
                    SOURCE: /Users/ljm/code/SicsthSense/cloud-altmanage/app/views/managePage.scala.html
                    HASH: 29aa2962309c5fae43f54528c613a1de6c6c8866
                    MATRIX: 736->1|856->28|884->47|920->49|953->74|992->76|1334->383|1349->389|1399->430|1439->432|1760->722|1818->745|1836->754|1850->759|1889->760|1925->777|1958->788|2102->897|2117->903|2167->944|2207->946|2529->1237|2584->1257|2602->1266|2623->1278|2662->1279|2698->1296|2760->1336|2868->1408|2883->1414|2938->1447|3151->1632|3180->1633|3511->1937|3539->1938|3587->1955
                    LINES: 26->1|30->1|32->4|33->5|33->5|33->5|51->23|51->23|51->23|51->23|57->29|60->32|60->32|60->32|60->32|60->32|60->32|67->39|67->39|67->39|67->39|73->45|75->47|75->47|75->47|75->47|75->47|75->47|82->54|82->54|82->54|87->59|87->59|91->63|91->63|94->66
                    -- GENERATED --
                */
            