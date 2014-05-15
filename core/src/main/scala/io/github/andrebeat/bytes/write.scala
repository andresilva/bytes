package io.github.andrebeat.bytes

trait Write[A] {
  def apply(bytes: Bytes, offset: Int, value: A): Unit
}

object Write {
  implicit object ByteWrite extends Write[Byte] {
    def apply(bytes: Bytes, offset: Int, value: Byte) = bytes.writeByte(offset, value)
  }

  implicit object ShortWrite extends Write[Short] {
    def apply(bytes: Bytes, offset: Int, value: Short) = bytes.writeShort(offset, value)
  }

  implicit object CharWrite extends Write[Char] {
    def apply(bytes: Bytes, offset: Int, value: Char) = bytes.writeChar(offset, value)
  }

  implicit object IntWrite extends Write[Int] {
    def apply(bytes: Bytes, offset: Int, value: Int) = bytes.writeInt(offset, value)
  }

  implicit object FloatWrite extends Write[Float] {
    def apply(bytes: Bytes, offset: Int, value: Float) = bytes.writeFloat(offset, value)
  }

  implicit object LongWrite extends Write[Long] {
    def apply(bytes: Bytes, offset: Int, value: Long) = bytes.writeLong(offset, value)
  }

  implicit object DoubleWrite extends Write[Double] {
    def apply(bytes: Bytes, offset: Int, value: Double) = bytes.writeDouble(offset, value)
  }
}
