organization := "com.gvolpe"

name := """akka-streams-full-project"""

version := "1.0.0-SNAPSHOT"

scalaVersion := "2.11.7"

val akkaStreamVersion = "2.0-M1"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream-experimental" % akkaStreamVersion,
  "com.typesafe.akka" %% "akka-stream-testkit-experimental" % akkaStreamVersion,
  "org.scalatest" %% "scalatest" % "2.2.4"
)

ScoverageSbtPlugin.ScoverageKeys.coverageExcludedPackages := ".*StreamsApp.*"
