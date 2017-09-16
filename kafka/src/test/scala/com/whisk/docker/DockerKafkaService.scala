package com.whisk.docker

import com.spotify.docker.client.messages.PortBinding
import com.whisk.docker.testkit.scalatest.DockerTestKitForAll
import com.whisk.docker.testkit.{Container, ContainerSpec, DockerReadyChecker, ManagedContainers}
import org.scalatest.Suite

trait DockerKafkaService extends DockerTestKitForAll {
  self: Suite =>

  val KafkaAdvertisedPort = 9092
  val ZookeeperPort = 2181

  val kafkaContainer: Container = ContainerSpec("spotify/kafka")
    .withPortBindings(KafkaAdvertisedPort -> PortBinding.of("0.0.0.0", KafkaAdvertisedPort),
                      ZookeeperPort -> PortBinding.of("0.0.0.0", ZookeeperPort))
    .withEnv(s"ADVERTISED_PORT=$KafkaAdvertisedPort", s"ADVERTISED_HOST=${dockerClient.getHost}")
    .withReadyChecker(DockerReadyChecker.LogLineContains("kafka entered RUNNING state"))
    .toContainer

  override val managedContainers: ManagedContainers =
    kafkaContainer.toManagedContainer
}
