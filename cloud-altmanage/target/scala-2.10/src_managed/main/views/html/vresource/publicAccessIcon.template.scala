
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
object publicAccessIcon extends BaseScalaTemplate[play.api.templates.Html,Format[play.api.templates.Html]](play.api.templates.HtmlFormat) with play.api.templates.Template3[Long,Boolean,Boolean,play.api.templates.Html] {

    /**/
    def apply/*1.2*/(id: Long, own: Boolean, isPublic: Boolean):play.api.templates.Html = {
        _display_ {

Seq[Any](format.raw/*1.45*/("""
 	"""),_display_(Seq[Any](/*2.4*/if(own)/*2.11*/ {_display_(Seq[Any](format.raw/*2.13*/("""
		"""),_display_(Seq[Any](/*3.4*/if(!isPublic)/*3.17*/ {_display_(Seq[Any](format.raw/*3.19*/("""
		  <a class="icon-btn icon-globe icon-white set_public_access_resource" parent_id=""""),_display_(Seq[Any](/*4.86*/id)),format.raw/*4.88*/("""" href="#" rel="tooltip" title="Share with the world!"></a>
		""")))}/*5.5*/else/*5.10*/{_display_(Seq[Any](format.raw/*5.11*/("""
		  <a class="icon-btn icon-globe remove_public_access_resource" parent_id=""""),_display_(Seq[Any](/*6.78*/id)),format.raw/*6.80*/("""" href="#" rel="tooltip" title="Protect from the world!"></a>
		""")))})),format.raw/*7.4*/("""
	""")))})),format.raw/*8.3*/("""
	"""))}
    }
    
    def render(id:Long,own:Boolean,isPublic:Boolean): play.api.templates.Html = apply(id,own,isPublic)
    
    def f:((Long,Boolean,Boolean) => play.api.templates.Html) = (id,own,isPublic) => apply(id,own,isPublic)
    
    def ref: this.type = this

}
                /*
                    -- GENERATED --
                    DATE: Mon Feb 11 23:20:21 CET 2013
                    SOURCE: /Users/ljm/code/SicsthSense/cloud-altmanage/app/views/vresource/publicAccessIcon.scala.html
                    HASH: 8a6ba93512a6f5ce7b39807d22e6e3b3ca1fd24b
                    MATRIX: 758->1|878->44|916->48|931->55|970->57|1008->61|1029->74|1068->76|1189->162|1212->164|1292->228|1304->233|1342->234|1455->312|1478->314|1573->379|1606->382
                    LINES: 26->1|29->1|30->2|30->2|30->2|31->3|31->3|31->3|32->4|32->4|33->5|33->5|33->5|34->6|34->6|35->7|36->8
                    -- GENERATED --
                */
            