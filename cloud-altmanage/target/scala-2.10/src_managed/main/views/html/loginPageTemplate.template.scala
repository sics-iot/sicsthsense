
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
object loginPageTemplate extends BaseScalaTemplate[play.api.templates.Html,Format[play.api.templates.Html]](play.api.templates.HtmlFormat) with play.api.templates.Template1[Html,play.api.templates.Html] {

    /**/
    def apply/*1.2*/(msg: Html = Html("")):play.api.templates.Html = {
        _display_ {import helper._


Seq[Any](format.raw/*1.24*/("""

"""),format.raw/*4.1*/("""
"""),_display_(Seq[Any](/*5.2*/layout("Home", null)/*5.22*/ {_display_(Seq[Any](format.raw/*5.24*/("""
    <!--
    """),_display_(Seq[Any](/*7.6*/if(msg != "")/*7.19*/ {_display_(Seq[Any](format.raw/*7.21*/("""
    	"""),_display_(Seq[Any](/*8.7*/msg)),format.raw/*8.10*/("""
    """)))})),format.raw/*9.6*/(""" 
    --> 
    <!-- Simple OpenID Selector -->
    <form action=""""),_display_(Seq[Any](/*12.20*/routes/*12.26*/.Login.authenticate(""))),format.raw/*12.49*/("""" method="get" id="openid_form">
      <input type="hidden" name="action" value="verify" />
      <fieldset>
        <legend>Sign-in with OpenID</legend>
        <div id="openid_choice">
          <p>Please click your account provider:</p>
          <div id="openid_btns"></div>
        </div>
        <div id="openid_input_area">
          <input id="openid_identifier" name="openid_identifier" type="url" required placeholder="Your OpenID" />
          <input id="openid_submit" type="submit" value="Log in" class="buttonLG"/>
        </div>
          <p>OpenID is service that allows you to log-on to many different websites using a single indentity.
          Find out <a href="http://openid.net/what/">more about OpenID</a> and <a href="http://openid.net/get/">how to get an OpenID enabled account</a>.</p>
      </fieldset>
    </form>    
""")))})),format.raw/*28.2*/("""
"""))}
    }
    
    def render(msg:Html): play.api.templates.Html = apply(msg)
    
    def f:((Html) => play.api.templates.Html) = (msg) => apply(msg)
    
    def ref: this.type = this

}
                /*
                    -- GENERATED --
                    DATE: Mon Feb 11 23:20:20 CET 2013
                    SOURCE: /Users/ljm/code/SicsthSense/cloud-altmanage/app/views/loginPageTemplate.scala.html
                    HASH: 8e6ef6a4310207ee10e6f79f1d5363d44155a962
                    MATRIX: 733->1|848->23|876->42|912->44|940->64|979->66|1028->81|1049->94|1088->96|1129->103|1153->106|1189->112|1291->178|1306->184|1351->207|2229->1054
                    LINES: 26->1|30->1|32->4|33->5|33->5|33->5|35->7|35->7|35->7|36->8|36->8|37->9|40->12|40->12|40->12|56->28
                    -- GENERATED --
                */
            