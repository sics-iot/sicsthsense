
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
object input extends BaseScalaTemplate[play.api.templates.Html,Format[play.api.templates.Html]](play.api.templates.HtmlFormat) with play.api.templates.Template5[Boolean,String,String,String,String,play.api.templates.Html] {

    /**/
    def apply/*1.2*/(edit: Boolean, itype: String, name: String, label: String, value: String):play.api.templates.Html = {
        _display_ {

Seq[Any](format.raw/*1.76*/("""

<dt>"""),_display_(Seq[Any](/*3.6*/label)),format.raw/*3.11*/("""</dt>
<dd>
"""),_display_(Seq[Any](/*5.2*/if(edit)/*5.10*/ {_display_(Seq[Any](format.raw/*5.12*/("""
<input type=""""),_display_(Seq[Any](/*6.15*/itype)),format.raw/*6.20*/("""" name=""""),_display_(Seq[Any](/*6.29*/name)),format.raw/*6.33*/("""" value=""""),_display_(Seq[Any](/*6.43*/value)),format.raw/*6.48*/("""" size="32" />
""")))}/*7.3*/else/*7.8*/{_display_(Seq[Any](format.raw/*7.9*/("""
"""),_display_(Seq[Any](/*8.2*/value)),format.raw/*8.7*/("""
""")))})),format.raw/*9.2*/("""
</dd>
"""))}
    }
    
    def render(edit:Boolean,itype:String,name:String,label:String,value:String): play.api.templates.Html = apply(edit,itype,name,label,value)
    
    def f:((Boolean,String,String,String,String) => play.api.templates.Html) = (edit,itype,name,label,value) => apply(edit,itype,name,label,value)
    
    def ref: this.type = this

}
                /*
                    -- GENERATED --
                    DATE: Mon Feb 11 23:20:21 CET 2013
                    SOURCE: /Users/ljm/code/SicsthSense/cloud-altmanage/app/views/forms/input.scala.html
                    HASH: 793c205de874d11ffcf70818b4306d48d599ef47
                    MATRIX: 758->1|909->75|952->84|978->89|1026->103|1042->111|1081->113|1132->129|1158->134|1202->143|1227->147|1272->157|1298->162|1332->180|1343->185|1380->186|1417->189|1442->194|1475->197
                    LINES: 26->1|29->1|31->3|31->3|33->5|33->5|33->5|34->6|34->6|34->6|34->6|34->6|34->6|35->7|35->7|35->7|36->8|36->8|37->9
                    -- GENERATED --
                */
            