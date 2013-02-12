
package views.html.vlabel

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
object list extends BaseScalaTemplate[play.api.templates.Html,Format[play.api.templates.Html]](play.api.templates.HtmlFormat) with play.api.templates.Template2[Html,List[String],play.api.templates.Html] {

    /**/
    def apply/*1.2*/(header: Html)(labels: List[String]):play.api.templates.Html = {
        _display_ {

Seq[Any](format.raw/*1.38*/("""

"""),_display_(Seq[Any](/*3.2*/if(labels.length > 0)/*3.23*/ {_display_(Seq[Any](format.raw/*3.25*/("""
  
  """),_display_(Seq[Any](/*5.4*/header)),format.raw/*5.10*/("""
  
  <ul>
    """),_display_(Seq[Any](/*8.6*/for(label <- labels) yield /*8.26*/ {_display_(Seq[Any](format.raw/*8.28*/("""
		<li><a href="">"""),_display_(Seq[Any](/*9.19*/label)),format.raw/*9.24*/("""</a> </li>
    """)))})),format.raw/*10.6*/("""
  </ul>
""")))})),format.raw/*12.2*/("""
"""))}
    }
    
    def render(header:Html,labels:List[String]): play.api.templates.Html = apply(header)(labels)
    
    def f:((Html) => (List[String]) => play.api.templates.Html) = (header) => (labels) => apply(header)(labels)
    
    def ref: this.type = this

}
                /*
                    -- GENERATED --
                    DATE: Mon Feb 11 23:20:21 CET 2013
                    SOURCE: /Users/ljm/code/SicsthSense/cloud-altmanage/app/views/vlabel/list.scala.html
                    HASH: 5d339230bcfb86bd83de5f641ff09568a02e77cd
                    MATRIX: 740->1|853->37|890->40|919->61|958->63|999->70|1026->76|1076->92|1111->112|1150->114|1204->133|1230->138|1277->154|1318->164
                    LINES: 26->1|29->1|31->3|31->3|31->3|33->5|33->5|36->8|36->8|36->8|37->9|37->9|38->10|40->12
                    -- GENERATED --
                */
            