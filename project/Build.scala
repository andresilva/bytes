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
    .aggregate(core)
    .settings(basicSettings: _*)

  lazy val core = Project("core", file("core"))
    .settings(basicSettings: _*)
}
