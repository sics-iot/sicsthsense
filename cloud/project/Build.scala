import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "SicsthSense"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      // Add your project dependencies here,
      "com.github.play2war" %% "play2-war-core" % "0.3",
      "mysql" % "mysql-connector-java" % "5.1.18"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
      // Add your own project settings here
      resolvers += "Play2war plugins release" at "http://repository-play-war.forge.cloudbees.com/release/"
    )

}
