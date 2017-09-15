name := "docker-testkit-kafka"

scalaVersion := "2.12.3"

libraryDependencies ++= Seq(
  "com.whisk" %% "docker-testkit-core" % "0.10.0-beta0" % "test",
  "org.apache.kafka" % "kafka-clients" % "0.11.0.0" % "test",
  "org.scalatest" %% "scalatest" % "3.0.4" % "test"
)