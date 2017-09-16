name := "docker-testkit-kafka"

scalaVersion := "2.12.3"

libraryDependencies ++= Seq(
  "com.whisk" %% "docker-testkit-scalatest" % "0.10.0-beta1" % "test",
  "org.apache.kafka" % "kafka-clients" % "0.11.0.0" % "test",
  "org.scalatest" %% "scalatest" % "3.0.4" % "test"
)