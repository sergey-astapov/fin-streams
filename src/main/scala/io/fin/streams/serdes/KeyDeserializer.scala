package io.fin.streams.serdes

import java.nio.ByteBuffer
import java.util

import io.fin.streams.api.Key
import org.apache.kafka.common.serialization.Deserializer

class KeyDeserializer extends Deserializer[Key] {
  override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}

  override def deserialize(topic: String, data: Array[Byte]): Key = {
    val buff = ByteBuffer.wrap(data)
    Key(readString(buff), readString(buff), readString(buff), readString(buff))
  }

  override def close(): Unit = {}
}