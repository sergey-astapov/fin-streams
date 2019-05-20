package io.fin.streams.serdes

import java.nio.ByteBuffer
import java.util

import io.fin.streams.api.ProxyAccount
import org.apache.kafka.common.serialization.Deserializer

class ProxyAccountDeserializer extends Deserializer[ProxyAccount] {
  override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}

  override def deserialize(topic: String, data: Array[Byte]): ProxyAccount = {
    val buff = ByteBuffer.wrap(data)
    ProxyAccount(readString(buff), readString(buff))
  }

  override def close(): Unit = {}
}