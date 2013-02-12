
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
object accountPage extends BaseScalaTemplate[play.api.templates.Html,Format[play.api.templates.Html]](play.api.templates.HtmlFormat) with play.api.templates.Template2[User,Form[User],play.api.templates.Html] {

    /**/
    def apply/*1.2*/(user: User, userForm: Form[User]):play.api.templates.Html = {
        _display_ {import helper._

def /*5.2*/displayUser/*5.13*/(user: User):play.api.templates.Html = {_display_(

Seq[Any](format.raw/*5.29*/("""
"""),_display_(Seq[Any](/*6.2*/if(userForm != null)/*6.22*/ {_display_(Seq[Any](format.raw/*6.24*/("""
<span class="logged"><a href=""""),_display_(Seq[Any](/*7.32*/routes/*7.38*/.CtrlUser.get())),format.raw/*7.53*/("""">[Cancel]</a></span>
""")))}/*8.3*/else/*8.8*/{_display_(Seq[Any](format.raw/*8.9*/("""
<span class="logged"><a href=""""),_display_(Seq[Any](/*9.32*/routes/*9.38*/.CtrlUser.edit())),format.raw/*9.54*/("""">[Edit]</a></span>
""")))})),format.raw/*10.2*/("""

   """),_display_(Seq[Any](/*12.5*/forms/*12.10*/.form(routes.CtrlUser.submit())/*12.41*/ {_display_(Seq[Any](format.raw/*12.43*/("""
	    <dt>Mail</dt>
	    <dd>"""),_display_(Seq[Any](/*14.11*/user/*14.15*/.email)),format.raw/*14.21*/("""</dd>
	    """),_display_(Seq[Any](/*15.7*/forms/*15.12*/.input(userForm!=null, "text", "userName", "User name", user.userName))),format.raw/*15.82*/("""
	    """),_display_(Seq[Any](/*16.7*/forms/*16.12*/.input(userForm!=null, "text", "firstName", "First name", user.firstName))),format.raw/*16.85*/("""
	    """),_display_(Seq[Any](/*17.7*/forms/*17.12*/.input(userForm!=null, "text", "lastName", "Last name", user.lastName))),format.raw/*17.82*/("""
	    """),_display_(Seq[Any](/*18.7*/forms/*18.12*/.input(userForm!=null, "text", "location", "Location", user.location))),format.raw/*18.81*/("""
	    """),_display_(Seq[Any](/*19.7*/if(userForm!=null)/*19.25*/ {_display_(Seq[Any](format.raw/*19.27*/(""" """),_display_(Seq[Any](/*19.29*/forms/*19.34*/.submit(userForm!=null, "Update"))),format.raw/*19.67*/(""" """)))})),format.raw/*19.69*/("""
  """)))})),format.raw/*20.4*/("""
  
""")))};
Seq[Any](format.raw/*1.36*/("""

"""),format.raw/*4.1*/("""
"""),format.raw/*22.2*/("""

"""),_display_(Seq[Any](/*24.2*/layout("Account", session)/*24.28*/ {_display_(Seq[Any](format.raw/*24.30*/("""
  """),_display_(Seq[Any](/*25.4*/displayUser(user))),format.raw/*25.21*/("""
""")))})),format.raw/*26.2*/("""
"""))}
    }
    
    def render(user:User,userForm:Form[User]): play.api.templates.Html = apply(user,userForm)
    
    def f:((User,Form[User]) => play.api.templates.Html) = (user,userForm) => apply(user,userForm)
    
    def ref: this.type = this

}
                /*
                    -- GENERATED --
                    DATE: Mon Feb 11 23:20:20 CET 2013
                    SOURCE: /Users/ljm/code/SicsthSense/cloud-altmanage/app/views/accountPage.scala.html
                    HASH: 3549b9403e1fde14afe6a890ac3cbdbd06538560
                    MATRIX: 738->1|848->56|867->67|946->83|982->85|1010->105|1049->107|1116->139|1130->145|1166->160|1206->184|1217->189|1254->190|1321->222|1335->228|1372->244|1424->265|1465->271|1479->276|1519->307|1559->309|1625->339|1638->343|1666->349|1713->361|1727->366|1819->436|1861->443|1875->448|1970->521|2012->528|2026->533|2118->603|2160->610|2174->615|2265->684|2307->691|2334->709|2374->711|2412->713|2426->718|2481->751|2515->753|2550->757|2594->35|2622->54|2650->762|2688->765|2723->791|2763->793|2802->797|2841->814|2874->816
                    LINES: 26->1|29->5|29->5|31->5|32->6|32->6|32->6|33->7|33->7|33->7|34->8|34->8|34->8|35->9|35->9|35->9|36->10|38->12|38->12|38->12|38->12|40->14|40->14|40->14|41->15|41->15|41->15|42->16|42->16|42->16|43->17|43->17|43->17|44->18|44->18|44->18|45->19|45->19|45->19|45->19|45->19|45->19|45->19|46->20|49->1|51->4|52->22|54->24|54->24|54->24|55->25|55->25|56->26
                    -- GENERATED --
                */
            