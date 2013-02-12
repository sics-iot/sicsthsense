
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
object managePageNew extends BaseScalaTemplate[play.api.templates.Html,Format[play.api.templates.Html]](play.api.templates.HtmlFormat) with play.api.templates.Template1[List[EndPoint],play.api.templates.Html] {

    /**/
    def apply/*1.2*/(endPoints: List[EndPoint]):play.api.templates.Html = {
        _display_ {import helper._


Seq[Any](format.raw/*1.29*/("""

"""),format.raw/*4.1*/("""
"""),_display_(Seq[Any](/*5.2*/layout("Manage", session)/*5.27*/ {_display_(Seq[Any](format.raw/*5.29*/("""
 
  <h1>Register new device</h1>
 
   """),_display_(Seq[Any](/*9.5*/helper/*9.11*/.form(action = routes.CtrlEndPoint.add())/*9.52*/ {_display_(Seq[Any](format.raw/*9.54*/("""
    <dl>
      <input input type="text" name="label" required placeholder="Label of the device" class="inputL" />
      <input input type="url" name="url" placeholder="Publically reachable URL (optional)" class="inputL" />
      <input type="submit" value="Register" class="buttonLG"/>
    </dl>
  """)))})),format.raw/*15.4*/("""
  
  """),_display_(Seq[Any](/*17.4*/vendpoint/*17.13*/.list/*17.18*/{_display_(Seq[Any](format.raw/*17.19*/("""<h1>Your end points</h1>""")))}/*17.44*/(endPoints))),format.raw/*17.55*/("""
  """),_display_(Seq[Any](/*18.4*/vresource/*18.13*/.listStreams/*18.25*/{_display_(Seq[Any](format.raw/*18.26*/("""<h1>Your resources</h1>""")))}/*18.50*/(Resource.getByUser(CtrlUser.getUser())))),format.raw/*18.90*/("""
""")))})),format.raw/*19.2*/("""

 
 
   <div class="container-fluid">  
     <div class="accordion" id="accordion2">  
            <div class="accordion-group">  
              <div class="accordion-heading">  
                <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion2" href="#collapseOne">  
                  Click me to exapand. Click me again to collapse. Part I.  
                </a>  
              </div>  
              <div id="collapseOne" class="accordion-body collapse" style="height: 0px; ">  
                <div class="accordion-inner">  
                  Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.  
                </div>  
              </div>  
            </div>  
            <div class="accordion-group">  
              <div class="accordion-heading">  
                <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion2" href="#collapseTwo">  
                 Click me to exapand. Click me again to collapse. Part II.  
                </a>  
              </div>  
              <div id="collapseTwo" class="accordion-body collapse">  
                <div class="accordion-inner">  
                  Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.  
                </div>  
              </div>  
            </div>  
            <div class="accordion-group">  
              <div class="accordion-heading">  
                <a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion2" href="#collapseThree">  
                  Click me to exapand. Click me again to collapse. Part III.  
                </a>  
              </div>  
              <div id="collapseThree" class="accordion-body collapse">  
                <div class="accordion-inner">  
                  Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.  
                </div>  
              </div>  
            </div>  
          </div>  
    </div>  
    <script type="text/javascript" src="/twitter-bootstrap/twitter-bootstrap-v2/docs/assets/js/jquery.js"></script>  
    <script type="text/javascript" src="/twitter-bootstrap/twitter-bootstrap-v2/docs/assets/js/bootstrap-collapse.js"></script>  
</body>  
</html>  """))}
    }
    
    def render(endPoints:List[EndPoint]): play.api.templates.Html = apply(endPoints)
    
    def f:((List[EndPoint]) => play.api.templates.Html) = (endPoints) => apply(endPoints)
    
    def ref: this.type = this

}
                /*
                    -- GENERATED --
                    DATE: Mon Feb 11 23:20:21 CET 2013
                    SOURCE: /Users/ljm/code/SicsthSense/cloud-altmanage/app/views/managePageNew.scala.html
                    HASH: 7934aaff445524d906ec20166863ae1a21d94b2e
                    MATRIX: 739->1|859->28|887->47|923->49|956->74|995->76|1069->116|1083->122|1132->163|1171->165|1502->465|1544->472|1562->481|1576->486|1615->487|1659->512|1692->523|1731->527|1749->536|1770->548|1809->549|1852->573|1914->613|1947->615
                    LINES: 26->1|30->1|32->4|33->5|33->5|33->5|37->9|37->9|37->9|37->9|43->15|45->17|45->17|45->17|45->17|45->17|45->17|46->18|46->18|46->18|46->18|46->18|46->18|47->19
                    -- GENERATED --
                */
            