
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
object layout extends BaseScalaTemplate[play.api.templates.Html,Format[play.api.templates.Html]](play.api.templates.HtmlFormat) with play.api.templates.Template3[String,Http.Session,Html,play.api.templates.Html] {

    /**/
    def apply/*1.2*/(title: String, session: Http.Session)(content: Html):play.api.templates.Html = {
        _display_ {import helper._


Seq[Any](format.raw/*1.55*/(""" """),format.raw/*2.1*/("""
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<title>SicsthSense - """),_display_(Seq[Any](/*7.23*/title)),format.raw/*7.28*/("""</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">  
<meta name="description" content="SicsthSense IoT cloud enabler">
<!-- Bootstrap -->
<link rel="stylesheet" media="screen" href=""""),_display_(Seq[Any](/*11.46*/routes/*11.52*/.Assets.at("stylesheets/bootstrap.css"))),format.raw/*11.91*/("""" />
<link rel="shortcut icon" type="image/png" href=""""),_display_(Seq[Any](/*12.51*/routes/*12.57*/.Assets.at("images/favicon.png"))),format.raw/*12.89*/("""" />
<!-- <link rel="stylesheet" media="screen" href=""""),_display_(Seq[Any](/*13.51*/routes/*13.57*/.Assets.at("stylesheets/main.css"))),format.raw/*13.91*/("""" /> -->
<link rel="stylesheet" media="screen" href=""""),_display_(Seq[Any](/*14.46*/routes/*14.52*/.Assets.at("stylesheets/mystyles.css"))),format.raw/*14.90*/("""" />
<link rel="stylesheet" type="text/css" href=""""),_display_(Seq[Any](/*15.47*/routes/*15.53*/.Assets.at("stylesheets/openid.css"))),format.raw/*15.89*/("""" />
<script src=""""),_display_(Seq[Any](/*16.15*/routes/*16.21*/.Assets.at("javascripts/jquery-1.9.0.min.js"))),format.raw/*16.66*/("""" type="text/javascript"></script>
<script src=""""),_display_(Seq[Any](/*17.15*/routes/*17.21*/.Application.javascriptRoutes())),format.raw/*17.52*/("""" type="text/javascript" ></script>
 
<style type="text/css">  
.socials """),format.raw/*20.10*/("""{"""),format.raw/*20.11*/("""  
padding: 10px;  
"""),format.raw/*22.1*/("""}"""),format.raw/*22.2*/("""  
</style>
</head>
<body>
<div id="fb-root"></div>
	<script>
		(function(d, s, id) """),format.raw/*28.23*/("""{"""),format.raw/*28.24*/("""
			var js, fjs = d.getElementsByTagName(s)[0];
			if (d.getElementById(id))
				return;
			js = d.createElement(s);
			js.id = id;
			js.src = "http://connect.facebook.net/en_GB/all.js#xfbml=1";
			fjs.parentNode.insertBefore(js, fjs);
		"""),format.raw/*36.3*/("""}"""),format.raw/*36.4*/("""(document, 'script', 'facebook-jssdk'));
	</script>

	<!--navigation bar-->
	<div class="navbar navbar-static-top navbar-inverse">
		<div class="navbar-inner">
			<div class="container">

				<!--navigation bar links-->
				<ul class="nav">
					<li>
					<a class="brand" href=""""),_display_(Seq[Any](/*47.30*/routes/*47.36*/.Application.home())),format.raw/*47.55*/("""">
						<Strong>Sics<sup>th</sup>Sense</Strong></a></li> 
					"""),_display_(Seq[Any](/*49.7*/if(session != null && session.get("id") != null)/*49.55*/ {_display_(Seq[Any](format.raw/*49.57*/("""
					<li><a href=""""),_display_(Seq[Any](/*50.20*/routes/*50.26*/.Application.home())),format.raw/*50.45*/("""">Home</a></li>
					<li><a href=""""),_display_(Seq[Any](/*51.20*/routes/*51.26*/.Application.search())),format.raw/*51.47*/("""">Search</a></li>
					<li><a href=""""),_display_(Seq[Any](/*52.20*/routes/*52.26*/.Application.manage())),format.raw/*52.47*/("""">Manage</a></li>
					<li><a href=""""),_display_(Seq[Any](/*53.20*/routes/*53.26*/.CtrlUser.get())),format.raw/*53.41*/("""">Account</a></li>
					""")))}/*54.8*/else/*54.13*/{_display_(Seq[Any](format.raw/*54.14*/("""
					<li><a href=""""),_display_(Seq[Any](/*55.20*/routes/*55.26*/.Application.home())),format.raw/*55.45*/("""">Log in</a></li> 
					""")))})),format.raw/*56.7*/("""
					<li><a href=""""),_display_(Seq[Any](/*57.20*/routes/*57.26*/.Public.about())),format.raw/*57.41*/("""">API</a></li> 
				</ul>
				
				<!-- search bar -->
				<form class="navbar-search pull-left">
					<input type="text" class="search-query" placeholder="Search">
				</form>
				
				<!-- User menu -->
				 """),_display_(Seq[Any](/*66.7*/if(session != null && session.get("id") != null &&
					User.get(session.get("id").toLong) != null)/*67.49*/ {_display_(Seq[Any](format.raw/*67.51*/("""
		    <ul class="nav pull-right ">		   
			    <li class="dropdown">
				    <a href="#" class="dropdown-toggle" 
				    data-toggle="dropdown">
				    <i class="icon-user icon-white"> </i>"""),_display_(Seq[Any](/*72.47*/User/*72.51*/.get(session.get("id").toLong).userName)),format.raw/*72.90*/("""
				    	<b class="caret"></b>
				    </a>
				    <ul class="dropdown-menu">
					    <li><a href=""""),_display_(Seq[Any](/*76.24*/routes/*76.30*/.CtrlUser.get())),format.raw/*76.45*/("""">View profile</a></li>
					    <li><a href=""""),_display_(Seq[Any](/*77.24*/routes/*77.30*/.CtrlUser.edit())),format.raw/*77.46*/("""">Edit profile</a></li>
					    <li><a href="/logout">Logout</a></li>
				    </ul>
			    </li>
		    </ul>
     		""")))})),format.raw/*82.9*/("""
     		
     		<!-- Social menu -->
				<ul class="nav pull-right">
					<li class="dropdown"><a href="#" class="dropdown-toggle"
						data-toggle="dropdown"> Social <b class="caret"></b>
					</a>
						<ul class="dropdown-menu">
							<li class="socials">
								<!-- Place this tag where you want the +1 button to render --> <g:plusone
									annotation="inline" width="150"></g:plusone>
							</li>
							<li class="socials"><a href="https://twitter.com/share"
								class="twitter-share-button">Tweet</a> <script>
									!function(d, s, id) """),format.raw/*96.30*/("""{"""),format.raw/*96.31*/("""
										var js, fjs = d.getElementsByTagName(s)[0];
										if (!d.getElementById(id)) """),format.raw/*98.38*/("""{"""),format.raw/*98.39*/("""
											js = d.createElement(s);
											js.id = id;
											js.src = "//platform.twitter.com/widgets.js";
											fjs.parentNode.insertBefore(js, fjs);
										"""),format.raw/*103.11*/("""}"""),format.raw/*103.12*/("""
									"""),format.raw/*104.10*/("""}"""),format.raw/*104.11*/("""(document, "script", "twitter-wjs");
								</script></li>
								<li class="socials"><div class="fb-like" data-send="false"
									data-width="150" data-show-faces="true"></div></li>
						</ul></li>
				</ul>
			</div>
		</div>
	</div><!--navigation bar end-->

	<!-- Alerts! -->
	<div class="container-errormsg">	

  </div> <!-- /container-errormsg -->
	
	<!-- Page contents -->
	<div class="container">"""),_display_(Seq[Any](/*120.26*/content)),format.raw/*120.33*/("""</div> <!-- /container -->
	
	<!-- Scripts at the end, to make loading faster -->
	<script src=""""),_display_(Seq[Any](/*123.16*/routes/*123.22*/.Assets.at("javascripts/jquery.dateFormat-1.0.js"))),format.raw/*123.72*/("""" type="text/javascript"></script>
	<script src=""""),_display_(Seq[Any](/*124.16*/routes/*124.22*/.Assets.at("javascripts/streamplots.js"))),format.raw/*124.62*/("""" type="text/javascript"></script>
	<script src=""""),_display_(Seq[Any](/*125.16*/routes/*125.22*/.Assets.at("javascripts/bootstrap.js"))),format.raw/*125.60*/("""" type="text/javascript"></script>
	<script src=""""),_display_(Seq[Any](/*126.16*/routes/*126.22*/.Assets.at("javascripts/jquery.flot.js"))),format.raw/*126.62*/("""" type="text/javascript"></script>
	<script src=""""),_display_(Seq[Any](/*127.16*/routes/*127.22*/.Assets.at("javascripts/openid-jquery.js"))),format.raw/*127.64*/("""" type="text/javascript"></script>
	<script src=""""),_display_(Seq[Any](/*128.16*/routes/*128.22*/.Assets.at("javascripts/openid-en.js"))),format.raw/*128.60*/("""" type="text/javascript"></script>
	
	<script type="text/javascript">
		$(document).ready(function() """),format.raw/*131.32*/("""{"""),format.raw/*131.33*/("""
			openid.init('openid_identifier');
		"""),format.raw/*133.3*/("""}"""),format.raw/*133.4*/(""");
	</script>
	<script type="text/javascript">  
  (function() """),format.raw/*136.15*/("""{"""),format.raw/*136.16*/("""  
    var po = document.createElement('script'); po.type = 'text/javascript'; po.async = true;  
    po.src = 'https://apis.google.com/js/plusone.js';  
    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(po, s);  
  """),format.raw/*140.3*/("""}"""),format.raw/*140.4*/(""")();  
</script>

<script src=""""),_display_(Seq[Any](/*143.15*/routes/*143.21*/.Assets.at("javascripts/myscripts.js"))),format.raw/*143.59*/("""" type="text/javascript"></script>
 
</body>
</html>
"""))}
    }
    
    def render(title:String,session:Http.Session,content:Html): play.api.templates.Html = apply(title,session)(content)
    
    def f:((String,Http.Session) => (Html) => play.api.templates.Html) = (title,session) => (content) => apply(title,session)(content)
    
    def ref: this.type = this

}
                /*
                    -- GENERATED --
                    DATE: Mon Feb 11 23:20:20 CET 2013
                    SOURCE: /Users/ljm/code/SicsthSense/cloud-altmanage/app/views/layout.scala.html
                    HASH: 696df329f5e4751c7e012de304e841245be9e724
                    MATRIX: 742->1|888->54|915->72|1036->158|1062->163|1310->375|1325->381|1386->420|1477->475|1492->481|1546->513|1637->568|1652->574|1708->608|1798->662|1813->668|1873->706|1960->757|1975->763|2033->799|2088->818|2103->824|2170->869|2255->918|2270->924|2323->955|2424->1028|2453->1029|2500->1049|2528->1050|2640->1134|2669->1135|2935->1374|2963->1375|3279->1655|3294->1661|3335->1680|3435->1745|3492->1793|3532->1795|3588->1815|3603->1821|3644->1840|3715->1875|3730->1881|3773->1902|3846->1939|3861->1945|3904->1966|3977->2003|3992->2009|4029->2024|4072->2050|4085->2055|4124->2056|4180->2076|4195->2082|4236->2101|4292->2126|4348->2146|4363->2152|4400->2167|4646->2378|4754->2477|4794->2479|5023->2672|5036->2676|5097->2715|5236->2818|5251->2824|5288->2839|5371->2886|5386->2892|5424->2908|5573->3026|6159->3584|6188->3585|6308->3677|6337->3678|6542->3854|6572->3855|6611->3865|6641->3866|7089->4277|7119->4284|7253->4381|7269->4387|7342->4437|7429->4487|7445->4493|7508->4533|7595->4583|7611->4589|7672->4627|7759->4677|7775->4683|7838->4723|7925->4773|7941->4779|8006->4821|8093->4871|8109->4877|8170->4915|8300->5016|8330->5017|8398->5057|8427->5058|8519->5121|8549->5122|8825->5370|8854->5371|8923->5403|8939->5409|9000->5447
                    LINES: 26->1|30->1|30->2|35->7|35->7|39->11|39->11|39->11|40->12|40->12|40->12|41->13|41->13|41->13|42->14|42->14|42->14|43->15|43->15|43->15|44->16|44->16|44->16|45->17|45->17|45->17|48->20|48->20|50->22|50->22|56->28|56->28|64->36|64->36|75->47|75->47|75->47|77->49|77->49|77->49|78->50|78->50|78->50|79->51|79->51|79->51|80->52|80->52|80->52|81->53|81->53|81->53|82->54|82->54|82->54|83->55|83->55|83->55|84->56|85->57|85->57|85->57|94->66|95->67|95->67|100->72|100->72|100->72|104->76|104->76|104->76|105->77|105->77|105->77|110->82|124->96|124->96|126->98|126->98|131->103|131->103|132->104|132->104|148->120|148->120|151->123|151->123|151->123|152->124|152->124|152->124|153->125|153->125|153->125|154->126|154->126|154->126|155->127|155->127|155->127|156->128|156->128|156->128|159->131|159->131|161->133|161->133|164->136|164->136|168->140|168->140|171->143|171->143|171->143
                    -- GENERATED --
                */
            