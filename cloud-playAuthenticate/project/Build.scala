import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

    val appName         = "SicsthSense2"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      // Add your project dependencies here,
        javaCore, javaJdbc, javaEbean,
      "be.objectify"  %%  "deadbolt-java"     % "2.1-SNAPSHOT",
      "com.feth"      %%  "play-authenticate" % "0.2.4-SNAPSHOT",
      "mysql" % "mysql-connector-java" % "5.1.18",
      "commons-io" % "commons-io" % "2.3",
      "commons-codec" % "commons-codec" % "1.7"  
    )

    val main = play.Project(appName, appVersion, appDependencies).settings(
      // Add your own project settings here
      resolvers += Resolver.url("Objectify Play Repository (release)", url("http://schaloner.github.com/releases/"))(Resolver.ivyStylePatterns),
      resolvers += Resolver.url("Objectify Play Repository (snapshot)", url("http://schaloner.github.com/snapshots/"))(Resolver.ivyStylePatterns),

      resolvers += Resolver.url("play-easymail (release)", url("http://joscha.github.com/play-easymail/repo/releases/"))(Resolver.ivyStylePatterns),
      resolvers += Resolver.url("play-easymail (snapshot)", url("http://joscha.github.com/play-easymail/repo/snapshots/"))(Resolver.ivyStylePatterns),

      resolvers += Resolver.url("play-authenticate (release)", url("http://joscha.github.com/play-authenticate/repo/releases/"))(Resolver.ivyStylePatterns),
      resolvers += Resolver.url("play-authenticate (snapshot)", url("http://joscha.github.com/play-authenticate/repo/snapshots/"))(Resolver.ivyStylePatterns)      
    )

}
