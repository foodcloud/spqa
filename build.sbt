name := "metro-spqa"
organization := "org.foodcloud"
version := "0.1"

scalaVersion := "2.12.6"

val playVersion = "2.6.15"

crossScalaVersions := Seq("2.11.8", "2.12.6")

publishTo := Some("releases" at "https://mymavenrepo.com/repo/ou8dJuHGO5zQVKWnbV4k/")

libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-api" % "1.7.25",
  "org.apache.qpid" % "qpid-broker" % "7.0.3",
  "com.typesafe.play" %% "play" % playVersion,
  "com.typesafe.play" %% "play-cache" % playVersion,
  "com.typesafe.akka" %% "akka-http" % "10.1.1",
  "com.lightbend.akka" %% "akka-stream-alpakka-amqp" % "0.19",

  "org.scalatest" %% "scalatest" % "3.0.5" % Test
)