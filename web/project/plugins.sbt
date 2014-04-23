// Comment to get more information during initialization
logLevel := Level.Warn

// The Typesafe repository
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"
//resolvers += "Typesafe repository" at http://repo.typesafe.com/typesafe/ivy-releases/

//dependencyOverrides += "org.avaje.ebeanorm" % "avaje-ebeanorm-agent" % "3.2.2","org.avaje.ebeanorm" % "avaje-ebeanorm" % "3.3.1-RC2"


// Use the Play sbt plugin for Play projects
//addSbtPlugin("play" % "sbt-plugin" % "2.1.0")
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.2.3-RC2")
//addSbtPlugin("com.typesafe.play" % "sbt-plugin" % Option(System.getProperty("play.version")).getOrElse("2.0"))

