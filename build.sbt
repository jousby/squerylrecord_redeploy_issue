organization := "ouso"

name := "portfolio-manager"

version := "0.1"

scalaVersion := "2.9.0-1"

retrieveManaged := false

logLevel := Level.Warn

jettyScanDirs := Nil

seq(webSettings :_*)

libraryDependencies ++= {
  val liftVersion = "2.4-M2"
  Seq(
    "net.liftweb" %% "lift-webkit" % liftVersion % "compile->default" withSources(),
    "net.liftweb" %% "lift-squeryl-record" % liftVersion % "compile->default" withSources()
  )
}

libraryDependencies ++= Seq(
    "com.h2database" % "h2" % "1.2.138" withSources(),
    "ch.qos.logback" % "logback-classic" % "0.9.26" withSources(),
    "org.mortbay.jetty" % "jetty" % "6.1.22" % "jetty,test" withSources(),
    "junit" % "junit" % "4.8.2" % "test->default" withSources(),
    "org.scalatest" % "scalatest_2.9.0" % "1.6.1" % "test->default" withSources()
  )


resolvers += "Jetty Repo" at "http://repo1.maven.org/maven2/org/mortbay/jetty"

