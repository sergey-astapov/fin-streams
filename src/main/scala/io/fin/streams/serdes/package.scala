package io.fin.streams

import java.nio.ByteBuffer

import org.apache.commons.lang3.StringUtils

package object serdes {
  def readString(buffer: ByteBuffer): String = {
    val len = buffer.getInt
    val buf = new Array[Byte](len)
    buffer.get(buf)
    new String(buf)
  }

  def writeString(buffer: ByteBuffer, s: String): Unit = {
    buffer.putInt(StringUtils.length(s))
    Option(s).foreach(str => buffer.put(str.getBytes))
  }
}
