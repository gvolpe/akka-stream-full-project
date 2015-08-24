organization := "com.gvolpe"

name := """events-processor-akka-streams"""

version := "1.0.0-SNAPSHOT"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream-experimental" % "1.0",
  "com.typesafe.akka" %% "akka-stream-testkit-experimental" % "1.0",
  "org.scalatest" %% "scalatest" % "2.2.4"
)

ScoverageSbtPlugin.ScoverageKeys.coverageExcludedPackages := ".*StreamsApp.*"
