name := """lunatech"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,
  "org.scalacheck" %% "scalacheck" % "1.12.5" % Test,
  "com.nrinaudo" % "kantan.csv_2.11" % "0.1.12",
  "com.nrinaudo" %% "kantan.csv-generic" % "0.1.12"
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

routesGenerator := InjectedRoutesGenerator
