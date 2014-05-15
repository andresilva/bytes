package io.github.andrebeat.bytes

trait Read[A] {
  def apply(bytes: Bytes, offset: Int): A
}

object Read {
  implicit object ByteRead extends Read[Byte] {
    def apply(bytes: Bytes, offset: Int) = bytes.readByte(offset)
  }

  implicit object ShortRead extends Read[Short] {
    def apply(bytes: Bytes, offset: Int) = bytes.readShort(offset)
  }

  implicit object CharRead extends Read[Char] {
    def apply(bytes: Bytes, offset: Int) = bytes.readChar(offset)
  }

  implicit object IntRead extends Read[Int] {
    def apply(bytes: Bytes, offset: Int) = bytes.readInt(offset)
  }

  implicit object FloatRead extends Read[Float] {
    def apply(bytes: Bytes, offset: Int) = bytes.readFloat(offset)
  }

  implicit object LongRead extends Read[Long] {
    def apply(bytes: Bytes, offset: Int) = bytes.readLong(offset)
  }

  implicit object DoubleRead extends Read[Double] {
    def apply(bytes: Bytes, offset: Int) = bytes.readDouble(offset)
  }
}
