
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
object form extends BaseScalaTemplate[play.api.templates.Html,Format[play.api.templates.Html]](play.api.templates.HtmlFormat) with play.api.templates.Template2[play.api.mvc.Call,Html,play.api.templates.Html] {

    /**/
    def apply/*1.2*/(theAction: play.api.mvc.Call)(content: Html):play.api.templates.Html = {
        _display_ {

Seq[Any](format.raw/*1.47*/("""

"""),_display_(Seq[Any](/*3.2*/helper/*3.8*/.form(action = theAction)/*3.33*/ {_display_(Seq[Any](format.raw/*3.35*/("""
<dl>
"""),_display_(Seq[Any](/*5.2*/content)),format.raw/*5.9*/("""
</dl>
<!-- <div style="clear:both"/> -->
""")))})),format.raw/*8.2*/("""
"""))}
    }
    
    def render(theAction:play.api.mvc.Call,content:Html): play.api.templates.Html = apply(theAction)(content)
    
    def f:((play.api.mvc.Call) => (Html) => play.api.templates.Html) = (theAction) => (content) => apply(theAction)(content)
    
    def ref: this.type = this

}
                /*
                    -- GENERATED --
                    DATE: Mon Feb 11 23:20:21 CET 2013
                    SOURCE: /Users/ljm/code/SicsthSense/cloud-altmanage/app/views/forms/form.scala.html
                    HASH: aa109d7e0c362629eebe3a58c59cfc42e64c9ce1
                    MATRIX: 744->1|866->46|905->51|918->57|951->82|990->84|1033->93|1060->100|1136->146
                    LINES: 26->1|29->1|31->3|31->3|31->3|31->3|33->5|33->5|36->8
                    -- GENERATED --
                */
            