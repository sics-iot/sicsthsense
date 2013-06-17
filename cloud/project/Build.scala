import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName = "SicsthSense2"
  val appVersion = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    javaCore, javaJdbc, javaEbean,
    "mysql" % "mysql-connector-java" % "5.1.18",
    "commons-io" % "commons-io" % "2.3",
    "commons-codec" % "commons-codec" % "1.6",
    "com.clever-age" % "play2-elasticsearch" % "0.5.4",
    "org.apache.httpcomponents" % "httpcore" % "4.2.4",
    "com.netflix.rxjava" % "rxjava-scala" % "0.9.0"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here
    ebeanEnabled := true,
    resolvers += Resolver.url("play-plugin-releases", new URL("http://repo.scala-sbt.org/scalasbt/sbt-plugin-releases/"))(Resolver.ivyStylePatterns),
    resolvers += Resolver.url("play-plugin-snapshots", new URL("http://repo.scala-sbt.org/scalasbt/sbt-plugin-snapshots/"))(Resolver.ivyStylePatterns)
  )

}
