package io.github.andrebeat.bytes

import shapeless.{Generic, HList, HNil, ::}

trait Read[A] {
  def apply(bytes: Bytes, offset: Int): (A, Int)
}

object Read {
  def apply[A: Read](bytes: Bytes, offset: Int): (A, Int) =
    implicitly[Read[A]].apply(bytes, offset)

  implicit object ByteRead extends Read[Byte] {
    def apply(bytes: Bytes, offset: Int) = (bytes.readByte(offset), 1)
  }

  implicit object ShortRead extends Read[Short] {
    def apply(bytes: Bytes, offset: Int) = (bytes.readShort(offset), 2)
  }

  implicit object CharRead extends Read[Char] {
    def apply(bytes: Bytes, offset: Int) = (bytes.readChar(offset), 2)
  }

  implicit object IntRead extends Read[Int] {
    def apply(bytes: Bytes, offset: Int) = (bytes.readInt(offset), 4)
  }

  implicit object FloatRead extends Read[Float] {
    def apply(bytes: Bytes, offset: Int) = (bytes.readFloat(offset), 4)
  }

  implicit object LongRead extends Read[Long] {
    def apply(bytes: Bytes, offset: Int) = (bytes.readLong(offset), 8)
  }

  implicit object DoubleRead extends Read[Double] {
    def apply(bytes: Bytes, offset: Int) = (bytes.readDouble(offset), 8)
  }

  implicit def hlistRead[H, T <: HList](implicit read: Read[H], tailRead: Read[T]) =
    new Read[H :: T] {
      def apply(bytes: Bytes, offset: Int) = {
        val (value, size) = read(bytes, offset)
        val (tail, tailSize) = tailRead(bytes, offset + size)
        (value :: tail, size + tailSize)
      }
    }

  implicit object HNilRead extends Read[HNil] {
    def apply(bytes: Bytes, offset: Int) = (HNil, 0)
  }

  implicit def productRead[P <: Product, L <: HList](implicit gen: Generic.Aux[P, L], read: Read[L]) =
    new Read[P] {
      def apply(bytes: Bytes, offset: Int) = {
        val (hl, size) = read(bytes, offset)
        (gen.from(hl), size)
      }
    }

  implicit def traversableRead[A](implicit read: Read[A]) =
    new Read[Traversable[A]] {
      def apply(bytes: Bytes, offset: Int) = {
        val length = bytes.readShort(offset)
        (0 until length).foldLeft((Vector[A](), 2)) { case ((acc, off), _) =>
          val (value, size) = read(bytes, offset + off)
          (acc :+ value, size + off)
        }
      }
    }
}
