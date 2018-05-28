name := "foodcloud-spqa"

version := "0.1"

scalaVersion := "2.12.6"

libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-api" % "1.7.25",
  "org.apache.qpid" % "qpid-broker" % "7.0.3",
  "com.typesafe.play" %% "play" % "2.6.15",
  "com.typesafe.akka" %% "akka-http" % "10.1.1",
  "com.typesafe.play" %% "play-cache" % "2.6.15",
  "com.lightbend.akka" %% "akka-stream-alpakka-amqp" % "0.19",

  "org.scalatest" %% "scalatest" % "3.0.5" % Test
)