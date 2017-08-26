name := "Dina HuAPI"
version := "0.1.0"
organization := "code"
scalaVersion := "2.12.2"


enablePlugins(JettyPlugin)

libraryDependencies ++= {
  val liftVersion = "3.1.0"
  Seq(
    "net.liftweb"       %% "lift-webkit" % liftVersion % "compile",
    "ch.qos.logback" % "logback-classic" % "1.2.3", 
    "org.scalatest" %% "scalatest" % "3.0.1"   
  )
}

concurrentRestrictions in Global += Tags.limit(Tags.Test, 1)
