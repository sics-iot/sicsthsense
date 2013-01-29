import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "SicsthSense2"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      // Add your project dependencies here,
      "mysql" % "mysql-connector-java" % "5.1.18",
      "commons-io" % "commons-io" % "2.3",
      "commons-codec" % "commons-codec" % "1.7",
      "securesocial" % "securesocial_2.9.1" % "2.0.9"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = JAVA).settings(
      // Add your own project settings here
        resolvers += Resolver.url("SecureSocial Repository", url("http://securesocial.ws/repository/releases/"))(Resolver.ivyStylePatterns)
    )

}
