package io.fin.streams.serdes

import java.util

import io.fin.streams.api.ProxyAccount
import org.apache.kafka.common.serialization.{Deserializer, Serde, Serializer}

class ProxyAccountSerde extends Serde[ProxyAccount] {
  override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}

  override def close(): Unit = {}

  override def serializer(): Serializer[ProxyAccount] = new ProxyAccountSerializer

  override def deserializer(): Deserializer[ProxyAccount] = new ProxyAccountDeserializer
}
