package io.github.andrebeat.bytes

import shapeless.{Generic, HList, HNil, ::}

trait Write[-A] {
  def apply(bytes: Bytes, offset: Int, value: A): Int
  protected def apply(bytes: Bytes, offset: Int, totalSize: Int, value: A): Int =
    apply(bytes, offset, value)
}

object Write {
  def apply[A: Write](bytes: Bytes, offset: Int, value: A): Int =
    implicitly[Write[A]].apply(bytes, offset, value)

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

  implicit def hlistWrite[H, T <: HList](implicit write: Write[H], tailWrite: Write[T]) =
    new Write[H :: T] {
      def apply(bytes: Bytes, offset: Int, value: H :: T) = {
        val size = write(bytes, offset, value.head)
        tailWrite(bytes, offset + size, size, value.tail)
      }
      override def apply(bytes: Bytes, offset: Int, totalSize: Int, value: H :: T) = {
        val size = write(bytes, offset, value.head)
        tailWrite(bytes, offset + size, totalSize + size, value.tail)
      }
    }

  implicit object HNilWrite extends Write[HNil] {
    def apply(bytes: Bytes, offset: Int, value: HNil) = 0
    override def apply(bytes: Bytes, offset: Int, totalSize: Int, value: HNil) = totalSize
  }

  implicit def productWrite[P <: Product, L <: HList](implicit gen: Generic.Aux[P, L], write: Write[L]) =
    new Write[P] {
      def apply(bytes: Bytes, offset: Int, value: P) = write(bytes, offset, gen.to(value))
    }
}
