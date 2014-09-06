package io.github.andrebeat.bytes

import scala.collection.generic.CanBuildFrom
import shapeless.{Generic, HList, HNil, ::}

trait Read[A] {
  def apply(bytes: Bytes, offset: Int): (A, Int)
}

trait LowPriorityReadImplicts {
  implicit def mapCbfRead[A, B, M[A, B] <: Traversable[(A, B)]](implicit
    read: Read[Traversable[(A, B)]],
    cbf: CanBuildFrom[Nothing, (A, B), M[A, B]]) =
    new Read[M[A, B]] {
      def apply(bytes: Bytes, offset: Int) = {
        val (traversable, size) = read(bytes, offset)
        val builder = cbf()
        builder.sizeHint(size)
        builder ++= traversable
        (builder.result, size)
      }
    }
}

object Read extends LowPriorityReadImplicts {
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

  implicit def eitherRead[A, B](implicit reada: Read[A], readb: Read[B]) =
    new Read[Either[A, B]] {
      def apply(bytes: Bytes, offset: Int) = {
        bytes.readByte(offset) match {
          case 0 =>
            val (a, s) = reada(bytes, offset + 1)
            (Left(a), s + 1)
          case 1 =>
            val (b, s) = readb(bytes, offset + 1)
            (Right(b), s + 1)
        }
      }
    }

  implicit def optionRead[A](implicit read: Read[A]) =
    new Read[Option[A]] {
      def apply(bytes: Bytes, offset: Int) = {
        bytes.readByte(offset) match {
          case 0 => (None, 1)
          case 1 =>
            val (a, s) = read(bytes, offset + 1)
            (Some(a), s + 1)
        }
      }
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

  implicit def cbfRead[A, C[A] <: Traversable[A]](implicit
    read: Read[Traversable[A]],
    cbf: CanBuildFrom[Nothing, A, C[A]]) =
    new Read[C[A]] {
      def apply(bytes: Bytes, offset: Int) = {
        val (traversable, size) = read(bytes, offset)
        val builder = cbf()
        builder.sizeHint(size)
        builder ++= traversable
        (builder.result, size)
      }
    }
}
