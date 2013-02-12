
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
object resourceToolbar extends BaseScalaTemplate[play.api.templates.Html,Format[play.api.templates.Html]](play.api.templates.HtmlFormat) with play.api.templates.Template4[Resource,Boolean,Boolean,Boolean,play.api.templates.Html] {

    /**/
    def apply/*1.2*/(resource: Resource, own: Boolean, shared: Boolean, publicAccess: Boolean):play.api.templates.Html = {
        _display_ {import helper._


Seq[Any](format.raw/*1.76*/("""
"""),format.raw/*3.1*/("""	
"""),_display_(Seq[Any](/*4.2*/if(own || shared || publicAccess)/*4.35*/ {_display_(Seq[Any](format.raw/*4.37*/("""
<!-- 	    	displayResource  		 -->
		<span class="dropdown-submenu">
	    <a class="icon-btn icon-zoom-in" tabindex="-1" href="#" rel="tooltip" title="Display"></a>
	    <ul class="dropdown-menu">
	   	<!-- displayResource dropdown menu -->
		  """),_display_(Seq[Any](/*10.6*/displayResource(resource, own, resource.getEndPoint(),resource.getUser()))),format.raw/*10.79*/("""
	    </ul>
	  </span>

	"""),_display_(Seq[Any](/*14.3*/if(resource.lastUpdated != 0)/*14.32*/ {_display_(Seq[Any](format.raw/*14.34*/("""
	 <span class="sideinfo">[updated """),_display_(Seq[Any](/*15.36*/Utils/*15.41*/.timeStr((Utils.currentTime()-resource.lastUpdated)))),format.raw/*15.93*/(""" ago]</span>
	 """)))}/*16.5*/else/*16.10*/{_display_(Seq[Any](format.raw/*16.11*/("""
	 	<span class="sideinfo">[never updated]</span>
	 """)))})),format.raw/*18.4*/("""
	 """),_display_(Seq[Any](/*19.4*/if(resource.pollingPeriod != 0)/*19.35*/ {_display_(Seq[Any](format.raw/*19.37*/("""
	 	<span class="sideinfo">[polled every """),_display_(Seq[Any](/*20.42*/Utils/*20.47*/.timeStr(resource.pollingPeriod))),format.raw/*20.79*/("""]</span>
	""")))}/*21.4*/else/*21.9*/{_display_(Seq[Any](format.raw/*21.10*/("""
		<span class="sideinfo">[not polled]</span>
	""")))})),format.raw/*23.3*/("""
	
	<span class="rightControl">
	
	"""),_display_(Seq[Any](/*27.3*/vresource/*27.12*/.followIcon(resource, "rlist_toolbar"))),format.raw/*27.50*/("""
	
	"""),_display_(Seq[Any](/*29.3*/if(own)/*29.10*/ {_display_(Seq[Any](format.raw/*29.12*/("""
		 <a href=""""),_display_(Seq[Any](/*30.14*/routes/*30.20*/.CtrlResource.delete(resource.id))),format.raw/*30.53*/("""" class="icon-btn icon-trash delete" rel="tooltip" title="Delete"></a>
		 """),_display_(Seq[Any](/*31.5*/if(resource.hasData())/*31.27*/ {_display_(Seq[Any](format.raw/*31.29*/("""
	     <a href=""""),_display_(Seq[Any](/*32.17*/routes/*32.23*/.CtrlResource.clearStream(resource.id))),format.raw/*32.61*/("""" class="icon-btn icon-remove-sign delete" rel="tooltip" title="Clear"></a>
	   """)))}/*33.7*/else/*33.12*/{_display_(Seq[Any](format.raw/*33.13*/("""
	   	 <a href="#" class="icon-btn icon-remove-sign icon-white delete" rel="tooltip" title="Clear"></a>
	   """)))})),format.raw/*35.6*/("""
	   
		 """),_display_(Seq[Any](/*37.5*/if(resource.hasUrl())/*37.26*/ {_display_(Seq[Any](format.raw/*37.28*/("""
		 			<!-- polling pause -->
		     """),_display_(Seq[Any](/*39.9*/if(resource.pollingPeriod != 0)/*39.40*/ {_display_(Seq[Any](format.raw/*39.42*/("""
		    		<a href=""""),_display_(Seq[Any](/*40.19*/routes/*40.25*/.CtrlResource.setPeriod(resource.id, 0))),format.raw/*40.64*/("""" class="icon-btn icon-pause " rel="tooltip" title="Stop"></a>
		    	""")))}/*41.10*/else/*41.15*/{_display_(Seq[Any](format.raw/*41.16*/("""
		    		<a href="#" class="icon-btn icon-pause icon-white " rel="tooltip" title="Stop"></a>
		    	""")))})),format.raw/*43.9*/("""
		    	<!-- polling play -->
		    	<span class="dropdown-submenu">
			    <a class="icon-btn icon-play" tabindex="-1" href="#" rel="tooltip" title="Poll"></a>
			    <ul class="dropdown-menu">
			    <!-- polling dropdown menu -->
			    """),_display_(Seq[Any](/*49.9*/helper/*49.15*/.form(routes.CtrlResource.setPeriod(resource.id,0))/*49.66*/ {_display_(Seq[Any](format.raw/*49.68*/("""	  
			     <dt class="action inline"><strong>Enter polling interval</strong></dt>
        		<dd>	
					   		<input type="hidden" name="id" value=""""),_display_(Seq[Any](/*52.50*/resource/*52.58*/.id)),format.raw/*52.61*/("""" />
					    	<input type="number" class="input-mini" name="period" required placeholder="Period" value=""""),_display_(Seq[Any](/*53.103*/(resource.pollingPeriod/60))),format.raw/*53.130*/("""" rel="tooltip" title="Period"/>
					    	<input class="icon-btn icon-play btn-link" href="#" rel="tooltip" title="Poll" type="submit" value=""/>
				    	</dd>
					    """)))})),format.raw/*56.11*/("""
			    </ul>
			  </span>
    """)))})),format.raw/*59.6*/("""
    <!-- set regex -->
		<span class="dropdown-submenu">
	    <a class="icon-btn icon-wrench" tabindex="-1" href="#" rel="tooltip" title="Poll"></a>
	    <ul class="dropdown-menu">
	   	<!-- regex dropdown menu -->
		  """),_display_(Seq[Any](/*65.6*/form(routes.CtrlResource.setInputParser(resource.id,""))/*65.62*/ {_display_(Seq[Any](format.raw/*65.64*/("""
		    <dt class="action inline"><strong>Set input parser <a href="http://regexpal.com/">(regexp)</a></strong></dt>
	        <dd>  
	          <input input type="hidden" name="id" value=""""),_display_(Seq[Any](/*68.57*/resource/*68.65*/.id)),format.raw/*68.68*/("""" />
	          <input input type="text" name="parser" class="input-medium" required placeholder=""""),_display_(Seq[Any](/*69.95*/resource/*69.103*/.inputParser)),format.raw/*69.115*/("""" style="text-align:right;"/>
	          <input class="icon-btn icon-ok btn-link" href="#" rel="tooltip" title="Set RegEx" type="submit" value="" />
	        </dd>
	        """)))})),format.raw/*72.11*/("""
	    </ul>
	  </span>
    <!-- 		 publicAccessIcon -->
 		"""),_display_(Seq[Any](/*76.5*/if(!resource.isPublicAccess())/*76.35*/ {_display_(Seq[Any](format.raw/*76.37*/("""
		  <a class="icon-btn icon-globe icon-white set_public_access_resource" parent_id=""""),_display_(Seq[Any](/*77.86*/resource/*77.94*/.id)),format.raw/*77.97*/("""" href="#" rel="tooltip" title="Share with the world!"></a>
		""")))}/*78.5*/else/*78.10*/{_display_(Seq[Any](format.raw/*78.11*/("""
		  <a class="icon-btn icon-globe remove_public_access_resource" parent_id=""""),_display_(Seq[Any](/*79.78*/resource/*79.86*/.id)),format.raw/*79.89*/("""" href="#" rel="tooltip" title="Protect from the world!"></a>
		""")))})),format.raw/*80.4*/(""" 
	 """)))})),format.raw/*81.4*/("""
	 </span>
 """)))})),format.raw/*83.3*/("""

"""))}
    }
    
    def render(resource:Resource,own:Boolean,shared:Boolean,publicAccess:Boolean): play.api.templates.Html = apply(resource,own,shared,publicAccess)
    
    def f:((Resource,Boolean,Boolean,Boolean) => play.api.templates.Html) = (resource,own,shared,publicAccess) => apply(resource,own,shared,publicAccess)
    
    def ref: this.type = this

}
                /*
                    -- GENERATED --
                    DATE: Mon Feb 11 23:20:21 CET 2013
                    SOURCE: /Users/ljm/code/SicsthSense/cloud-altmanage/app/views/vresource/resourceToolbar.scala.html
                    HASH: c4f26c44a71cb1c7fd6dbe57a5d199055701197e
                    MATRIX: 769->1|936->75|963->93|1000->96|1041->129|1080->131|1362->378|1457->451|1518->477|1556->506|1596->508|1668->544|1682->549|1756->601|1790->618|1803->623|1842->624|1926->677|1965->681|2005->712|2045->714|2123->756|2137->761|2191->793|2220->805|2232->810|2271->811|2350->859|2421->895|2439->904|2499->942|2539->947|2555->954|2595->956|2645->970|2660->976|2715->1009|2825->1084|2856->1106|2896->1108|2949->1125|2964->1131|3024->1169|3123->1251|3136->1256|3175->1257|3315->1366|3360->1376|3390->1397|3430->1399|3503->1437|3543->1468|3583->1470|3638->1489|3653->1495|3714->1534|3804->1606|3817->1611|3856->1612|3988->1713|4264->1954|4279->1960|4339->2011|4379->2013|4563->2161|4580->2169|4605->2172|4749->2279|4799->2306|5003->2478|5066->2510|5322->2731|5387->2787|5427->2789|5651->2977|5668->2985|5693->2988|5828->3087|5846->3095|5881->3107|6087->3281|6182->3341|6221->3371|6261->3373|6383->3459|6400->3467|6425->3470|6506->3534|6519->3539|6558->3540|6672->3618|6689->3626|6714->3629|6810->3694|6846->3699|6890->3712
                    LINES: 26->1|30->1|31->3|32->4|32->4|32->4|38->10|38->10|42->14|42->14|42->14|43->15|43->15|43->15|44->16|44->16|44->16|46->18|47->19|47->19|47->19|48->20|48->20|48->20|49->21|49->21|49->21|51->23|55->27|55->27|55->27|57->29|57->29|57->29|58->30|58->30|58->30|59->31|59->31|59->31|60->32|60->32|60->32|61->33|61->33|61->33|63->35|65->37|65->37|65->37|67->39|67->39|67->39|68->40|68->40|68->40|69->41|69->41|69->41|71->43|77->49|77->49|77->49|77->49|80->52|80->52|80->52|81->53|81->53|84->56|87->59|93->65|93->65|93->65|96->68|96->68|96->68|97->69|97->69|97->69|100->72|104->76|104->76|104->76|105->77|105->77|105->77|106->78|106->78|106->78|107->79|107->79|107->79|108->80|109->81|111->83
                    -- GENERATED --
                */
            