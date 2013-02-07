import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "SicsthSense2"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      // Add your project dependencies here,
      "be.objectify"  %%  "deadbolt-2"        % "1.1.3-SNAPSHOT",
      "com.feth"      %%  "play-authenticate" % "0.2.3-SNAPSHOT",
      "mysql" % "mysql-connector-java" % "5.1.18",
      "commons-io" % "commons-io" % "2.3",
      "commons-codec" % "commons-codec" % "1.7"      
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = JAVA).settings(
      // Add your own project settings here
       resolvers += Resolver.url("Objectify Play Repository", url("http://schaloner.github.com/releases/"))(Resolver.ivyStylePatterns),
      resolvers += Resolver.url("Objectify Play Repository", url("http://schaloner.github.com/snapshots/"))(Resolver.ivyStylePatterns),

      resolvers += Resolver.url("play-easymail (release)", url("http://joscha.github.com/play-easymail/repo/releases/"))(Resolver.ivyStylePatterns),
      resolvers += Resolver.url("play-easymail (snapshot)", url("http://joscha.github.com/play-easymail/repo/snapshots/"))(Resolver.ivyStylePatterns),

      resolvers += Resolver.url("play-authenticate (release)", url("http://joscha.github.com/play-authenticate/repo/releases/"))(Resolver.ivyStylePatterns),
      resolvers += Resolver.url("play-authenticate (snapshot)", url("http://joscha.github.com/play-authenticate/repo/snapshots/"))(Resolver.ivyStylePatterns)
    )

}
