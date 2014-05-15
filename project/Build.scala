import sbt._
import sbt.Keys._

object MyBuild extends Build {

  lazy val basicSettings = Seq(
    organization := "io.github.andrebeat",

    startYear := Some(2014),
    licenses := Seq("MIT License" -> url("https://raw.githubusercontent.com/andrebeat/bytes/master/LICENSE")),

    scalaVersion := "2.11.0",
    version := "0.1")

  lazy val root = Project("root", file("."))
    .aggregate(core, benchmark)
    .settings(basicSettings: _*)

  lazy val core = Project("core", file("core"))
    .settings(basicSettings: _*)

  lazy val benchmark = Project("benchmark", file("benchmark"))
    .dependsOn(core)
    .settings(basicSettings: _*)
    .settings(
      libraryDependencies ++= Seq(
        "com.google.code.java-allocation-instrumenter" % "java-allocation-instrumenter" % "2.0",
        "com.google.code.gson"                         % "gson"                         % "1.7.1"
      ),
      fork in run := true,
      // we need to add the runtime classpath as a "-cp" argument to the `javaOptions in run`, otherwise caliper
      // will not see the right classpath and die with a ConfigurationException
      javaOptions in run ++= Seq("-cp",
        Attributed.data((fullClasspath in Runtime).value).mkString(":")))
}
