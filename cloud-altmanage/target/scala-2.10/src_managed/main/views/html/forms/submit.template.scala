
package views.html.forms

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
object submit extends BaseScalaTemplate[play.api.templates.Html,Format[play.api.templates.Html]](play.api.templates.HtmlFormat) with play.api.templates.Template2[Boolean,String,play.api.templates.Html] {

    /**/
    def apply/*1.2*/(edit: Boolean, label: String):play.api.templates.Html = {
        _display_ {

Seq[Any](format.raw/*1.32*/("""

"""),_display_(Seq[Any](/*3.2*/if(edit)/*3.10*/ {_display_(Seq[Any](format.raw/*3.12*/("""
<dt></dt>
<dd>
<input type="submit" value=""""),_display_(Seq[Any](/*6.30*/label)),format.raw/*6.35*/("""" class="buttonG"/>
</dd>
""")))}/*8.3*/else/*8.8*/{_display_(Seq[Any](format.raw/*8.9*/("""
<input type="submit" value=""""),_display_(Seq[Any](/*9.30*/label)),format.raw/*9.35*/("""" class="buttonG" style="visibility: hidden;"/>
""")))})),format.raw/*10.2*/("""
"""))}
    }
    
    def render(edit:Boolean,label:String): play.api.templates.Html = apply(edit,label)
    
    def f:((Boolean,String) => play.api.templates.Html) = (edit,label) => apply(edit,label)
    
    def ref: this.type = this

}
                /*
                    -- GENERATED --
                    DATE: Mon Feb 11 23:20:21 CET 2013
                    SOURCE: /Users/ljm/code/SicsthSense/cloud-altmanage/app/views/forms/submit.scala.html
                    HASH: 159ee25e30782563ec5d6a140c3776ea25f51bc8
                    MATRIX: 738->1|845->31|884->36|900->44|939->46|1022->94|1048->99|1094->129|1105->134|1142->135|1208->166|1234->171|1315->221
                    LINES: 26->1|29->1|31->3|31->3|31->3|34->6|34->6|36->8|36->8|36->8|37->9|37->9|38->10
                    -- GENERATED --
                */
            