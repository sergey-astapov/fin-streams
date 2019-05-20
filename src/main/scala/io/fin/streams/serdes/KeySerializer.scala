package io.fin.streams.serdes

import java.nio.ByteBuffer
import java.util

import io.fin.streams.api.Key
import org.apache.kafka.common.serialization.Serializer

class KeySerializer extends Serializer[Key] {
  override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}

  override def serialize(topic: String, data: Key): Array[Byte] = {
    val buffer = ByteBuffer.allocate(data.account.length + 4 +
      data.amount.length + 4 +
      data.currency.length + 4 +
      data.date.length + 4
    )
    writeString(buffer, data.account)
    writeString(buffer, data.amount)
    writeString(buffer, data.currency)
    writeString(buffer, data.date)
    buffer.array()
  }

  override def close(): Unit = {}
}
