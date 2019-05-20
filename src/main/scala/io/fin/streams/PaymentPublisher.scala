package io.fin.streams

import java.io.InputStream
import java.nio.charset.Charset
import java.util.Properties

import io.fin.streams.api.{GcgMessage, Key, ProxyAccount}
import org.apache.commons.io.IOUtils
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord, RecordMetadata}

import scala.xml.XML

object PaymentPublisher extends App {
  private val gcgMsg = resource("data/gcg/FRN2019051600017633341.xml")
  val gcgKey = Key.apply(GcgMessage(XML.loadString(gcgMsg)))

  print(gcgKey)
  send(gcgMsg)
  send(ProxyAccount("4487153", "44709405882"))

  val rtaMsg = XML.loadString(resource("data/rta/447094058820000000039.xml"))

  private def resource(name: String) = {
    val is: InputStream = getClass.getClassLoader.getResourceAsStream(name)
    IOUtils.toString(is, Charset.defaultCharset())
  }

  private def send(msg: String) = {
    val properties = new Properties
    properties.put("bootstrap.servers", "localhost:19092,localhost:29092")
    properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    properties.put("acks", "1")
    properties.put("retries", "3")
    properties.put("compression.type", "snappy")
    //This line in for demonstration purposes
    //properties.put("partitioner.class", classOf[PurchaseKeyPartitioner].getName)

    val producer = new KafkaProducer[String, String](properties)
    val record = new ProducerRecord[String, String]("gcg-topic", "", msg)
    val callback = (metadata: RecordMetadata, exception: Exception) => {
      if (exception != null) exception.printStackTrace()
    }
    val sendFuture = producer.send(record,
      (_: RecordMetadata, exception: Exception) => Option(exception)
        .foreach(_.printStackTrace()))
  }

  private def send(proxyAccount: ProxyAccount) = {
    val properties = new Properties
    properties.put("bootstrap.servers", "localhost:19092,localhost:29092")
    properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    properties.put("value.serializer", "io.fin.streams.serdes.ProxyAccountSerializer")
    properties.put("acks", "1")
    properties.put("retries", "3")
    properties.put("compression.type", "snappy")
    //This line in for demonstration purposes
    //properties.put("partitioner.class", classOf[PurchaseKeyPartitioner].getName)

    val producer = new KafkaProducer[String, ProxyAccount](properties)
    val record = new ProducerRecord[String, ProxyAccount]("proxy-account-topic", "", proxyAccount)
    val callback = (metadata: RecordMetadata, exception: Exception) => {
      if (exception != null) exception.printStackTrace()
    }
    val sendFuture = producer.send(record,
      (_: RecordMetadata, exception: Exception) => Option(exception)
        .foreach(_.printStackTrace()))
  }
}
