package io.fin.streams.serdes

import java.nio.ByteBuffer
import java.util

import io.fin.streams.api.ProxyAccount
import org.apache.kafka.common.serialization.Serializer

class ProxyAccountSerializer extends Serializer[ProxyAccount] {
  override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}

  override def serialize(topic: String, data: ProxyAccount): Array[Byte] = {
    val buffer = ByteBuffer.allocate(data.proxy.length + 4 +
      data.account.length + 4
    )
    writeString(buffer, data.proxy)
    writeString(buffer, data.account)
    buffer.array()
  }

  override def close(): Unit = {}
}
