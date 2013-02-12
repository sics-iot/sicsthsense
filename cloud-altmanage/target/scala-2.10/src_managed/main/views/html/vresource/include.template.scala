
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
object include extends BaseScalaTemplate[play.api.templates.Html,Format[play.api.templates.Html]](play.api.templates.HtmlFormat) with play.api.templates.Template1[Resource,play.api.templates.Html] {

    /**/
    def apply/*1.2*/(resource: Resource):play.api.templates.Html = {
        _display_ {import helper._

def /*5.2*/displayResource/*5.17*/(endPoint: EndPoint):play.api.templates.Html = {_display_(

Seq[Any](format.raw/*5.41*/("""
	    
	 """),_display_(Seq[Any](/*7.4*/if(endPoint.hasUrl())/*7.25*/ {_display_(Seq[Any](format.raw/*7.27*/("""
      <dl>
      <dt class="action">Proxy</dt>
      <dd>
        <form id="proxy"""),_display_(Seq[Any](/*11.25*/resource/*11.33*/.id)),format.raw/*11.36*/("""" action=""""),_display_(Seq[Any](/*11.47*/routes/*11.53*/.Proxy.forwardById(resource.id, ""))),format.raw/*11.88*/("""">
	       <select id="method"""),_display_(Seq[Any](/*12.28*/resource/*12.36*/.id)),format.raw/*12.39*/("""" name="method" >
           <option value="GET">GET</option>
           <option value="POST">POST</option>
           <option value="PUT">PUT</option>
           <option value="DELETE">DELETE</option>
         </select>
         <input input type="text" id="arguments"""),_display_(Seq[Any](/*18.49*/resource/*18.57*/.id)),format.raw/*18.60*/("""" name="arguments" placeholder="arguments" size="8"/>
         <input input type="text" id="body"""),_display_(Seq[Any](/*19.44*/resource/*19.52*/.id)),format.raw/*19.55*/("""" name="body" placeholder="body" size="8"/>
         <input type="submit" value="Send" class="buttonG"/>
        </form>
        <img id="ajax_wait"""),_display_(Seq[Any](/*22.28*/resource/*22.36*/.id)),format.raw/*22.39*/("""" src=""""),_display_(Seq[Any](/*22.47*/routes/*22.53*/.Assets.at("images/ajax_wait.gif"))),format.raw/*22.87*/("""" height="20" align="top" style="visibility:hidden;"></img>
      </dd>
      
      <div id="response"""),_display_(Seq[Any](/*25.25*/resource/*25.33*/.id)),format.raw/*25.36*/("""" style="display: none">
	      <dt class="action">
	        <span id="response1"""),_display_(Seq[Any](/*27.30*/resource/*27.38*/.id)),format.raw/*27.41*/(""""></span>
	      </dt>
	      <dd>
	        <code><span id="response2"""),_display_(Seq[Any](/*30.36*/resource/*30.44*/.id)),format.raw/*30.47*/(""""></span></code> <a id="hide"""),_display_(Seq[Any](/*30.76*/resource/*30.84*/.id)),format.raw/*30.87*/("""" href="." class="delete">[hide]</a></dd>
	      </dd>
	      
      </div>
       </dl>
     
     <script type="text/javascript">
	  $('#proxy"""),_display_(Seq[Any](/*37.14*/resource/*37.22*/.id)),format.raw/*37.25*/("""').submit(function() """),format.raw/*37.46*/("""{"""),format.raw/*37.47*/("""
	    var method = $("select#method"""),_display_(Seq[Any](/*38.36*/resource/*38.44*/.id)),format.raw/*38.47*/("""").val();
	    var arguments = $("input#arguments"""),_display_(Seq[Any](/*39.41*/resource/*39.49*/.id)),format.raw/*39.52*/("""").val();
	    var url = """"),_display_(Seq[Any](/*40.18*/routes/*40.24*/.Proxy.forwardById(resource.id, ""))),format.raw/*40.59*/("""" + arguments;
	    var body = $("input#body"""),_display_(Seq[Any](/*41.31*/resource/*41.39*/.id)),format.raw/*41.42*/("""").val();
	    $("#ajax_wait"""),_display_(Seq[Any](/*42.20*/resource/*42.28*/.id)),format.raw/*42.31*/("""").css("visibility","visible");
	    $.ajax("""),format.raw/*43.13*/("""{"""),format.raw/*43.14*/("""
	    	  url: url,
	    	  context: document.body,
	    	  timeout: 30000,
	    	  type: method,
	    	  data: body,
	    	  contentType: "text/plain",
    	    error: function(x, t, m) """),format.raw/*50.35*/("""{"""),format.raw/*50.36*/("""
    	    	$("#response1"""),_display_(Seq[Any](/*51.25*/resource/*51.33*/.id)),format.raw/*51.36*/("""").html("Error");
    	    	$("#response2"""),_display_(Seq[Any](/*52.25*/resource/*52.33*/.id)),format.raw/*52.36*/("""").text(m);
            $("#response"""),_display_(Seq[Any](/*53.26*/resource/*53.34*/.id)),format.raw/*53.37*/("""").show();
    	    	$("#ajax_wait"""),_display_(Seq[Any](/*54.25*/resource/*54.33*/.id)),format.raw/*54.36*/("""").css("visibility","hidden");
    	    """),format.raw/*55.10*/("""}"""),format.raw/*55.11*/(""",
    	    success: function(response) """),format.raw/*56.38*/("""{"""),format.raw/*56.39*/("""
    	    	$("#response1"""),_display_(Seq[Any](/*57.25*/resource/*57.33*/.id)),format.raw/*57.36*/("""").html("Response");
            $("#response2"""),_display_(Seq[Any](/*58.27*/resource/*58.35*/.id)),format.raw/*58.38*/("""").text(response);
            $("#response"""),_display_(Seq[Any](/*59.26*/resource/*59.34*/.id)),format.raw/*59.37*/("""").show();
            $("#ajax_wait"""),_display_(Seq[Any](/*60.27*/resource/*60.35*/.id)),format.raw/*60.38*/("""").css("visibility","hidden");
           """),format.raw/*61.12*/("""}"""),format.raw/*61.13*/("""
	    	"""),format.raw/*62.7*/("""}"""),format.raw/*62.8*/(""");
	    			    
	    return false;
	  """),format.raw/*65.4*/("""}"""),format.raw/*65.5*/(""");
	  
	  $('#hide"""),_display_(Seq[Any](/*67.13*/resource/*67.21*/.id)),format.raw/*67.24*/("""').click(function() """),format.raw/*67.44*/("""{"""),format.raw/*67.45*/("""
		  $("#response"""),_display_(Seq[Any](/*68.18*/resource/*68.26*/.id)),format.raw/*68.29*/("""").hide();              
	      return false;
	    """),format.raw/*70.6*/("""}"""),format.raw/*70.7*/(""");
	  
	  </script>
	""")))})),format.raw/*73.3*/("""
""")))};
Seq[Any](format.raw/*1.22*/("""

"""),format.raw/*4.1*/("""
"""),format.raw/*74.2*/("""

"""),_display_(Seq[Any](/*76.2*/displayResource(resource.getEndPoint()))),format.raw/*76.41*/("""
"""))}
    }
    
    def render(resource:Resource): play.api.templates.Html = apply(resource)
    
    def f:((Resource) => play.api.templates.Html) = (resource) => apply(resource)
    
    def ref: this.type = this

}
                /*
                    -- GENERATED --
                    DATE: Mon Feb 11 23:20:21 CET 2013
                    SOURCE: /Users/ljm/code/SicsthSense/cloud-altmanage/app/views/vresource/include.scala.html
                    HASH: 96bab8fd74f7989e70ed792698ae6976c3bfed7c
                    MATRIX: 737->1|833->42|856->57|943->81|987->91|1016->112|1055->114|1174->197|1191->205|1216->208|1263->219|1278->225|1335->260|1401->290|1418->298|1443->301|1748->570|1765->578|1790->581|1923->678|1940->686|1965->689|2149->837|2166->845|2191->848|2235->856|2250->862|2306->896|2445->999|2462->1007|2487->1010|2604->1091|2621->1099|2646->1102|2752->1172|2769->1180|2794->1183|2859->1212|2876->1220|2901->1223|3082->1368|3099->1376|3124->1379|3173->1400|3202->1401|3274->1437|3291->1445|3316->1448|3402->1498|3419->1506|3444->1509|3507->1536|3522->1542|3579->1577|3660->1622|3677->1630|3702->1633|3767->1662|3784->1670|3809->1673|3881->1717|3910->1718|4124->1904|4153->1905|4214->1930|4231->1938|4256->1941|4334->1983|4351->1991|4376->1994|4449->2031|4466->2039|4491->2042|4562->2077|4579->2085|4604->2088|4672->2128|4701->2129|4768->2168|4797->2169|4858->2194|4875->2202|4900->2205|4983->2252|5000->2260|5025->2263|5105->2307|5122->2315|5147->2318|5220->2355|5237->2363|5262->2366|5332->2408|5361->2409|5395->2416|5423->2417|5488->2455|5516->2456|5571->2475|5588->2483|5613->2486|5661->2506|5690->2507|5744->2525|5761->2533|5786->2536|5864->2587|5892->2588|5945->2610|5986->21|6014->40|6042->2612|6080->2615|6141->2654
                    LINES: 26->1|29->5|29->5|31->5|33->7|33->7|33->7|37->11|37->11|37->11|37->11|37->11|37->11|38->12|38->12|38->12|44->18|44->18|44->18|45->19|45->19|45->19|48->22|48->22|48->22|48->22|48->22|48->22|51->25|51->25|51->25|53->27|53->27|53->27|56->30|56->30|56->30|56->30|56->30|56->30|63->37|63->37|63->37|63->37|63->37|64->38|64->38|64->38|65->39|65->39|65->39|66->40|66->40|66->40|67->41|67->41|67->41|68->42|68->42|68->42|69->43|69->43|76->50|76->50|77->51|77->51|77->51|78->52|78->52|78->52|79->53|79->53|79->53|80->54|80->54|80->54|81->55|81->55|82->56|82->56|83->57|83->57|83->57|84->58|84->58|84->58|85->59|85->59|85->59|86->60|86->60|86->60|87->61|87->61|88->62|88->62|91->65|91->65|93->67|93->67|93->67|93->67|93->67|94->68|94->68|94->68|96->70|96->70|99->73|101->1|103->4|104->74|106->76|106->76
                    -- GENERATED --
                */
            