
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
object searchPage extends BaseScalaTemplate[play.api.templates.Html,Format[play.api.templates.Html]](play.api.templates.HtmlFormat) with play.api.templates.Template0[play.api.templates.Html] {

    /**/
    def apply/*1.2*/():play.api.templates.Html = {
        _display_ {import helper._


Seq[Any](format.raw/*1.4*/("""

"""),format.raw/*4.1*/("""
"""),_display_(Seq[Any](/*5.2*/layout("Search", session)/*5.27*/ {_display_(Seq[Any](format.raw/*5.29*/("""
  
 """),_display_(Seq[Any](/*7.3*/vendpoint/*7.12*/.list/*7.17*/{_display_(Seq[Any](format.raw/*7.18*/("""<h1>Public resources</h1>""")))}/*7.44*/(EndPoint.all()))),format.raw/*7.60*/("""
 
""")))})),format.raw/*9.2*/("""
"""))}
    }
    
    def render(): play.api.templates.Html = apply()
    
    def f:(() => play.api.templates.Html) = () => apply()
    
    def ref: this.type = this

}
                /*
                    -- GENERATED --
                    DATE: Mon Feb 11 23:20:21 CET 2013
                    SOURCE: /Users/ljm/code/SicsthSense/cloud-altmanage/app/views/searchPage.scala.html
                    HASH: 0e70900fcf540001199f084f71a6542ee071c7c9
                    MATRIX: 721->1|815->3|843->22|879->24|912->49|951->51|991->57|1008->66|1021->71|1059->72|1103->98|1140->114|1174->118
                    LINES: 26->1|30->1|32->4|33->5|33->5|33->5|35->7|35->7|35->7|35->7|35->7|35->7|37->9
                    -- GENERATED --
                */
            