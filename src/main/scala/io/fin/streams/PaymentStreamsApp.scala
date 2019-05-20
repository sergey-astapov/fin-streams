package io.fin.streams

import java.util.Properties

import io.fin.streams.api.{GcgMessage, Key, ProxyAccount}
import io.fin.streams.serdes.{KeySerde, ProxyAccountSerde}
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.Serdes.StringSerde
import org.apache.kafka.streams.kstream.{JoinWindows, KStream, Printed, Produced}
import org.apache.kafka.streams.processor.WallclockTimestampExtractor
import org.apache.kafka.streams.{Consumed, KafkaStreams, StreamsBuilder, StreamsConfig}

import scala.xml.XML

object PaymentStreamsApp extends App {
  private def props = {
    val props = new Properties
    props.put(StreamsConfig.APPLICATION_ID_CONFIG, "payment_streams_app_id")
    props.put(StreamsConfig.CLIENT_ID_CONFIG, "payment_streams_app_client")
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "payment_group")
    props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:19092,localhost:29092")
    props.put(StreamsConfig.REPLICATION_FACTOR_CONFIG, "1")
    props.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, classOf[WallclockTimestampExtractor])
    props
  }

  val config = new StreamsConfig(props)

  val builder = new StreamsBuilder()
  val keySerde = new KeySerde()
  val stringSerde = new StringSerde()
  val gcgStream = builder.stream("gcg-topic", Consumed.`with`(stringSerde, stringSerde))
  val stream1: KStream[String, String] = gcgStream.selectKey((_, v) => Key(GcgMessage(XML.loadString(v))).account)
  val proxyAccountSerde = new ProxyAccountSerde()
  val proxyAccountStream: KStream[String, ProxyAccount] = builder.stream("proxy-account-topic", Consumed.`with`(stringSerde, proxyAccountSerde))
    .selectKey((_, v) => v.proxy)
  val paStream: KStream[String, String] = proxyAccountStream.mapValues(pa => pa.account)
  val joined: KStream[String, String] = stream1.join(paStream,
    (msg: String, acct: String) => GcgMessage(XML.loadString(msg)).withAccount(acct).xml.toString(),
    JoinWindows.of(60 * 1000 * 20),
    stringSerde, stringSerde, stringSerde
  )
  val changedGsgStream: KStream[Key, String] = joined.selectKey((_, v) => Key(GcgMessage(XML.loadString(v))))

  val stream2 = changedGsgStream.to("payment-out", Produced.`with`(keySerde, stringSerde))

  changedGsgStream.print(Printed.toSysOut[Key, String].withLabel("Payment App"))

  val streams = new KafkaStreams(builder.build(), config)
  streams.start()
  Thread.sleep(60000)
}
