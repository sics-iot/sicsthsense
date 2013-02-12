
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
object includeStream extends BaseScalaTemplate[play.api.templates.Html,Format[play.api.templates.Html]](play.api.templates.HtmlFormat) with play.api.templates.Template1[Resource,play.api.templates.Html] {

    /**/
    def apply/*1.2*/(stream: Resource):play.api.templates.Html = {
        _display_ {
def /*3.2*/WIN_5M/*3.8*/():play.api.templates.Html = {_display_(

Seq[Any](format.raw/*3.14*/("""5*60*1000""")))};def /*4.2*/WIN_30M/*4.9*/():play.api.templates.Html = {_display_(

Seq[Any](format.raw/*4.15*/("""30*60*1000""")))};def /*5.2*/WIN_1H/*5.8*/():play.api.templates.Html = {_display_(

Seq[Any](format.raw/*5.14*/("""60*60*1000""")))};def /*6.2*/WIN_1D/*6.8*/():play.api.templates.Html = {_display_(

Seq[Any](format.raw/*6.14*/("""24*60*60*1000""")))};def /*7.2*/WIN_1W/*7.8*/():play.api.templates.Html = {_display_(

Seq[Any](format.raw/*7.14*/("""7*24*60*60*1000""")))};def /*8.2*/WIN_1M/*8.8*/():play.api.templates.Html = {_display_(

Seq[Any](format.raw/*8.14*/("""30*24*60*60*1000""")))};def /*9.2*/WIN_1Y/*9.8*/():play.api.templates.Html = {_display_(

Seq[Any](format.raw/*9.14*/("""365*24*60*60*1000""")))};
Seq[Any](format.raw/*1.20*/("""

"""),format.raw/*3.24*/("""
"""),format.raw/*4.26*/("""
"""),format.raw/*5.25*/("""
"""),format.raw/*6.28*/("""
"""),format.raw/*7.30*/("""
"""),format.raw/*8.31*/("""
"""),format.raw/*9.32*/("""

"""),_display_(Seq[Any](/*11.2*/if(stream != null && stream.hasData())/*11.40*/ {_display_(Seq[Any](format.raw/*11.42*/("""
  
  
	<span id="streamconfig"""),_display_(Seq[Any](/*14.25*/{stream.id})),format.raw/*14.36*/("""" class="streamconfig form-inline">
	    <input type="radio" name="streamwindow"""),_display_(Seq[Any](/*15.45*/{stream.id})),format.raw/*15.56*/("""" value=""""),_display_(Seq[Any](/*15.66*/WIN_5M)),format.raw/*15.72*/("""" id="streamwindow"""),_display_(Seq[Any](/*15.91*/{stream.id})),format.raw/*15.102*/("""-1"><label for="streamwindow"""),_display_(Seq[Any](/*15.131*/{stream.id})),format.raw/*15.142*/("""-1">5min</label>
	    <input type="radio" name="streamwindow"""),_display_(Seq[Any](/*16.45*/{stream.id})),format.raw/*16.56*/("""" value=""""),_display_(Seq[Any](/*16.66*/WIN_30M)),format.raw/*16.73*/("""" id="streamwindow"""),_display_(Seq[Any](/*16.92*/{stream.id})),format.raw/*16.103*/("""-2"><label for="streamwindow"""),_display_(Seq[Any](/*16.132*/{stream.id})),format.raw/*16.143*/("""-2">30min</label>
	    <input type="radio" name="streamwindow"""),_display_(Seq[Any](/*17.45*/{stream.id})),format.raw/*17.56*/("""" value=""""),_display_(Seq[Any](/*17.66*/WIN_1H)),format.raw/*17.72*/("""" id="streamwindow"""),_display_(Seq[Any](/*17.91*/{stream.id})),format.raw/*17.102*/("""-3"><label for="streamwindow"""),_display_(Seq[Any](/*17.131*/{stream.id})),format.raw/*17.142*/("""-3">1h</label>
	    <input type="radio" name="streamwindow"""),_display_(Seq[Any](/*18.45*/{stream.id})),format.raw/*18.56*/("""" value=""""),_display_(Seq[Any](/*18.66*/WIN_1D)),format.raw/*18.72*/("""" id="streamwindow"""),_display_(Seq[Any](/*18.91*/{stream.id})),format.raw/*18.102*/("""-4"><label for="streamwindow"""),_display_(Seq[Any](/*18.131*/{stream.id})),format.raw/*18.142*/("""-4">1d</label>
	    <input type="radio" name="streamwindow"""),_display_(Seq[Any](/*19.45*/{stream.id})),format.raw/*19.56*/("""" value=""""),_display_(Seq[Any](/*19.66*/WIN_1W)),format.raw/*19.72*/("""" id="streamwindow"""),_display_(Seq[Any](/*19.91*/{stream.id})),format.raw/*19.102*/("""-5"><label for="streamwindow"""),_display_(Seq[Any](/*19.131*/{stream.id})),format.raw/*19.142*/("""-5">1w</label>
	    <input type="radio" name="streamwindow"""),_display_(Seq[Any](/*20.45*/{stream.id})),format.raw/*20.56*/("""" value=""""),_display_(Seq[Any](/*20.66*/WIN_1M)),format.raw/*20.72*/("""" id="streamwindow"""),_display_(Seq[Any](/*20.91*/{stream.id})),format.raw/*20.102*/("""-6"><label for="streamwindow"""),_display_(Seq[Any](/*20.131*/{stream.id})),format.raw/*20.142*/("""-6">1m</label>
	    <input type="radio" name="streamwindow"""),_display_(Seq[Any](/*21.45*/{stream.id})),format.raw/*21.56*/("""" value=""""),_display_(Seq[Any](/*21.66*/WIN_1Y)),format.raw/*21.72*/("""" id="streamwindow"""),_display_(Seq[Any](/*21.91*/{stream.id})),format.raw/*21.102*/("""-7"><label for="streamwindow"""),_display_(Seq[Any](/*21.131*/{stream.id})),format.raw/*21.142*/("""-7">1y</label>
	</span>
	<div id="streamtitle"""),_display_(Seq[Any](/*23.23*/{stream.id})),format.raw/*23.34*/("""" class="streamtitle">Data stream</div>
	<div id="streamplot"""),_display_(Seq[Any](/*24.22*/{stream.id})),format.raw/*24.33*/("""" class="streamplot"></div>

	<script type="text/javascript">
		// one global JavaScript object per stream
		var streamplot"""),_display_(Seq[Any](/*28.18*/{stream.id})),format.raw/*28.29*/(""" = new Object();
		streamplot"""),_display_(Seq[Any](/*29.14*/{stream.id})),format.raw/*29.25*/(""".id = 'streamplot"""),_display_(Seq[Any](/*29.43*/{stream.id})),format.raw/*29.54*/("""';
		streamplot"""),_display_(Seq[Any](/*30.14*/{stream.id})),format.raw/*30.25*/(""".uri = '"""),_display_(Seq[Any](/*30.34*/{routes.Streams.getSecured(stream.getEndPoint().getUser().userName, stream.getEndPoint().label, stream.path.substring(1))})),format.raw/*30.156*/("""';
		streamplot"""),_display_(Seq[Any](/*31.14*/{stream.id})),format.raw/*31.25*/(""".path = '"""),_display_(Seq[Any](/*31.35*/{stream.path.substring(1)})),format.raw/*31.61*/("""'; // for parsing the returned JSON
		streamplot"""),_display_(Seq[Any](/*32.14*/{stream.id})),format.raw/*32.25*/(""".window = """),_display_(Seq[Any](/*32.36*/WIN_1D)),format.raw/*32.42*/("""; // in ms
		streamplot"""),_display_(Seq[Any](/*33.14*/{stream.id})),format.raw/*33.25*/(""".since = parseInt(((new Date()).getTime() - streamplot"""),_display_(Seq[Any](/*33.80*/{stream.id})),format.raw/*33.91*/(""".window) / 1000); // in s for database
		streamplot"""),_display_(Seq[Any](/*34.14*/{stream.id})),format.raw/*34.25*/(""".points = [];
		streamplot"""),_display_(Seq[Any](/*35.14*/{stream.id})),format.raw/*35.25*/(""".timeout = null;

		$(function () """),format.raw/*37.17*/("""{"""),format.raw/*37.18*/("""
			// init Flot plot
			streamplot"""),_display_(Seq[Any](/*39.15*/{stream.id})),format.raw/*39.26*/(""".plot = $.plot($("#streamplot"""),_display_(Seq[Any](/*39.56*/{stream.id})),format.raw/*39.67*/(""""), [ streamplot"""),_display_(Seq[Any](/*39.84*/{stream.id})),format.raw/*39.95*/(""".points ], StreamPlots.options);
			// setup config form
			$("input:radio[name='streamwindow"""),_display_(Seq[Any](/*41.38*/{stream.id})),format.raw/*41.49*/("""']").filter('[value=""""),_display_(Seq[Any](/*41.71*/WIN_1D)),format.raw/*41.77*/(""""]').attr('checked', true);
			$("input:radio[name='streamwindow"""),_display_(Seq[Any](/*42.38*/{stream.id})),format.raw/*42.49*/("""']").change( function()"""),format.raw/*42.72*/("""{"""),format.raw/*42.73*/("""
				StreamPlots.setWindow(streamplot"""),_display_(Seq[Any](/*43.38*/{stream.id})),format.raw/*43.49*/(""", eval($("input:radio[name='streamwindow"""),_display_(Seq[Any](/*43.90*/{stream.id})),format.raw/*43.101*/("""']:checked").val()) ); """),format.raw/*43.124*/("""}"""),format.raw/*43.125*/(""");
			// start polling
			StreamPlots.poll(streamplot"""),_display_(Seq[Any](/*45.32*/{stream.id})),format.raw/*45.43*/(""");
		"""),format.raw/*46.3*/("""}"""),format.raw/*46.4*/(""");
	</script>
""")))})))}
    }
    
    def render(stream:Resource): play.api.templates.Html = apply(stream)
    
    def f:((Resource) => play.api.templates.Html) = (stream) => apply(stream)
    
    def ref: this.type = this

}
                /*
                    -- GENERATED --
                    DATE: Mon Feb 11 23:20:21 CET 2013
                    SOURCE: /Users/ljm/code/SicsthSense/cloud-altmanage/app/views/vresource/includeStream.scala.html
                    HASH: 0a8dc006348b11db11bf416652053b996bfa4713
                    MATRIX: 743->1|821->22|834->28|903->34|935->46|949->53|1018->59|1051->72|1064->78|1133->84|1166->97|1179->103|1248->109|1284->125|1297->131|1366->137|1404->155|1417->161|1486->167|1525->186|1538->192|1607->198|1664->19|1693->44|1721->70|1749->95|1777->123|1805->153|1833->184|1861->216|1899->219|1946->257|1986->259|2053->290|2086->301|2202->381|2235->392|2281->402|2309->408|2364->427|2398->438|2464->467|2498->478|2595->539|2628->550|2674->560|2703->567|2758->586|2792->597|2858->626|2892->637|2990->699|3023->710|3069->720|3097->726|3152->745|3186->756|3252->785|3286->796|3381->855|3414->866|3460->876|3488->882|3543->901|3577->912|3643->941|3677->952|3772->1011|3805->1022|3851->1032|3879->1038|3934->1057|3968->1068|4034->1097|4068->1108|4163->1167|4196->1178|4242->1188|4270->1194|4325->1213|4359->1224|4425->1253|4459->1264|4554->1323|4587->1334|4633->1344|4661->1350|4716->1369|4750->1380|4816->1409|4850->1420|4932->1466|4965->1477|5062->1538|5095->1549|5255->1673|5288->1684|5354->1714|5387->1725|5441->1743|5474->1754|5526->1770|5559->1781|5604->1790|5749->1912|5801->1928|5834->1939|5880->1949|5928->1975|6013->2024|6046->2035|6093->2046|6121->2052|6181->2076|6214->2087|6305->2142|6338->2153|6426->2205|6459->2216|6522->2243|6555->2254|6617->2288|6646->2289|6718->2325|6751->2336|6817->2366|6850->2377|6903->2394|6936->2405|7066->2499|7099->2510|7157->2532|7185->2538|7286->2603|7319->2614|7370->2637|7399->2638|7473->2676|7506->2687|7583->2728|7617->2739|7669->2762|7699->2763|7789->2817|7822->2828|7854->2833|7882->2834
                    LINES: 26->1|28->3|28->3|30->3|30->4|30->4|32->4|32->5|32->5|34->5|34->6|34->6|36->6|36->7|36->7|38->7|38->8|38->8|40->8|40->9|40->9|42->9|43->1|45->3|46->4|47->5|48->6|49->7|50->8|51->9|53->11|53->11|53->11|56->14|56->14|57->15|57->15|57->15|57->15|57->15|57->15|57->15|57->15|58->16|58->16|58->16|58->16|58->16|58->16|58->16|58->16|59->17|59->17|59->17|59->17|59->17|59->17|59->17|59->17|60->18|60->18|60->18|60->18|60->18|60->18|60->18|60->18|61->19|61->19|61->19|61->19|61->19|61->19|61->19|61->19|62->20|62->20|62->20|62->20|62->20|62->20|62->20|62->20|63->21|63->21|63->21|63->21|63->21|63->21|63->21|63->21|65->23|65->23|66->24|66->24|70->28|70->28|71->29|71->29|71->29|71->29|72->30|72->30|72->30|72->30|73->31|73->31|73->31|73->31|74->32|74->32|74->32|74->32|75->33|75->33|75->33|75->33|76->34|76->34|77->35|77->35|79->37|79->37|81->39|81->39|81->39|81->39|81->39|81->39|83->41|83->41|83->41|83->41|84->42|84->42|84->42|84->42|85->43|85->43|85->43|85->43|85->43|85->43|87->45|87->45|88->46|88->46
                    -- GENERATED --
                */
            