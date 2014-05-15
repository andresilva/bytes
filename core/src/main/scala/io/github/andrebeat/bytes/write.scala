package io.github.andrebeat.bytes

trait Write[A] {
  def apply(bytes: Bytes, offset: Int, value: A): Int
}

object Write {
  implicit object ByteWrite extends Write[Byte] {
    def apply(bytes: Bytes, offset: Int, value: Byte): Int = {
      bytes.writeByte(offset, value)
      1
    }
  }

  implicit object ShortWrite extends Write[Short] {
    def apply(bytes: Bytes, offset: Int, value: Short) = {
      bytes.writeShort(offset, value)
      2
    }
  }

  implicit object CharWrite extends Write[Char] {
    def apply(bytes: Bytes, offset: Int, value: Char) = {
      bytes.writeChar(offset, value)
      2
    }
  }

  implicit object IntWrite extends Write[Int] {
    def apply(bytes: Bytes, offset: Int, value: Int) = {
      bytes.writeInt(offset, value)
      4
    }
  }

  implicit object FloatWrite extends Write[Float] {
    def apply(bytes: Bytes, offset: Int, value: Float) = {
      bytes.writeFloat(offset, value)
      4
    }
  }

  implicit object LongWrite extends Write[Long] {
    def apply(bytes: Bytes, offset: Int, value: Long) = {
      bytes.writeLong(offset, value)
      8
    }
  }

  implicit object DoubleWrite extends Write[Double] {
    def apply(bytes: Bytes, offset: Int, value: Double) = {
      bytes.writeDouble(offset, value)
      8
    }
  }
}
