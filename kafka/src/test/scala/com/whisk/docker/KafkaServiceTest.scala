package com.whisk.docker

import java.util.{Collections, Properties}

import com.whisk.docker.testkit.ContainerState
import org.apache.kafka.clients.admin.{AdminClient, NewTopic}
import org.apache.kafka.clients.producer.{KafkaProducer, Producer, ProducerRecord, RecordMetadata}
import org.scalatest.FunSuite

class KafkaServiceTest extends FunSuite with DockerKafkaService {

  test("test container started") {
    assert(kafkaContainer.state().isInstanceOf[ContainerState.Ready], "kafka container is ready")
    println(kafkaContainer.mappedPorts()(KafkaAdvertisedPort))
    assert(kafkaContainer.mappedPorts().get(KafkaAdvertisedPort).nonEmpty, "kafka port is exposed")

    val adminClient = createAdminClient()

    def topics() = adminClient.listTopics().names().get()

    assert(topics().isEmpty, "topics should be empty initially")

    val topicName = "my-topic"

    adminClient.createTopics(Collections.singletonList(new NewTopic(topicName, 1, 1))).all().get()

    assert(topics().contains(topicName))

    val producer = createProducer()

    val res: RecordMetadata =
      producer.send(new ProducerRecord[String, String](topicName, "some-value")).get()
    assert(res.topic() == topicName, "offset should be returned")

    adminClient.close()
    producer.close()
  }

  private def createAdminClient(): AdminClient = {
    val props = new Properties()
    props.put("bootstrap.servers", "localhost:9092")
    props.put("client.id", "test")
    props.put("reconnect.backoff.ms", Integer.valueOf(1000))
    AdminClient.create(props)
  }

  private def createProducer(): Producer[String, String] = {
    val props = new Properties()
    props.put("bootstrap.servers", "localhost:9092")
    props.put("acks", "all")
    props.put("retries", Integer.valueOf(0))
    props.put("batch.size", Integer.valueOf(16384))
    props.put("linger.ms", Integer.valueOf(1))
    props.put("buffer.memory", Integer.valueOf(33554432))
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

    new KafkaProducer[String, String](props)
  }
}
