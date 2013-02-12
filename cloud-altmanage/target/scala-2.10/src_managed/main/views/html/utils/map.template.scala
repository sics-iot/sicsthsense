
package views.html.utils

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
object map extends BaseScalaTemplate[play.api.templates.Html,Format[play.api.templates.Html]](play.api.templates.HtmlFormat) with play.api.templates.Template1[String,play.api.templates.Html] {

    /**/
    def apply/*1.2*/(location: String):play.api.templates.Html = {
        _display_ {

Seq[Any](format.raw/*1.20*/("""

<iframe width="100%" height="400" frameborder="0" scrolling="no" marginheight="0" marginwidth="0" src=
"http://maps.google.com/maps?q="""),_display_(Seq[Any](/*4.33*/location)),format.raw/*4.41*/("""&amp;output=embed"
>
</iframe>
<br />
<small>
<a href=
"http://maps.google.com/maps?f=q&amp;source=embed&amp;hl=en&amp;geocode=&amp;q="""),_display_(Seq[Any](/*10.81*/location)),format.raw/*10.89*/("""&amp;ie=UTF8&amp;hq=&amp;hnear="""),_display_(Seq[Any](/*10.121*/location)),format.raw/*10.129*/("""&amp;t=m&amp;z=11"
style="text-align:left">View Larger Map</a></small>"""))}
    }
    
    def render(location:String): play.api.templates.Html = apply(location)
    
    def f:((String) => play.api.templates.Html) = (location) => apply(location)
    
    def ref: this.type = this

}
                /*
                    -- GENERATED --
                    DATE: Mon Feb 11 23:20:21 CET 2013
                    SOURCE: /Users/ljm/code/SicsthSense/cloud-altmanage/app/views/utils/map.scala.html
                    HASH: 1e93686fffc45fbb9078136f7fe9f8531ce858fc
                    MATRIX: 727->1|822->19|997->159|1026->167|1203->308|1233->316|1302->348|1333->356
                    LINES: 26->1|29->1|32->4|32->4|38->10|38->10|38->10|38->10
                    -- GENERATED --
                */
            