name := "sangria-playground"
description := "An example of GraphQL server written with Play and Sangria."

version := "1.0-SNAPSHOT"

scalaVersion := "2.13.3"

libraryDependencies ++= Seq(
  guice,
  filters,
  "org.sangria-graphql" %% "sangria" % "2.0.1",
  "org.sangria-graphql" %% "sangria-slowlog" % "2.0.1",
  "org.sangria-graphql" %% "sangria-play-json" % "2.0.1",
  "org.scalatest" %% "scalatest" % "3.1.4" % "test",
  "org.reactivemongo" %% "play2-reactivemongo" % "1.0.3-play28",
  "org.reactivemongo" %% "reactivemongo-play-json" % "0.20.13-play28"
)

routesGenerator := InjectedRoutesGenerator

lazy val root = (project in file(".")).enablePlugins(PlayScala)
