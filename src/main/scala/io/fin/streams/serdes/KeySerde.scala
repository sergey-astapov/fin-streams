package io.fin.streams.serdes

import java.util

import io.fin.streams.api.Key
import org.apache.kafka.common.serialization.{Deserializer, Serde, Serializer}

class KeySerde extends Serde[Key] {
  override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}

  override def close(): Unit = {}

  override def serializer(): Serializer[Key] = new KeySerializer

  override def deserializer(): Deserializer[Key] = new KeyDeserializer
}
