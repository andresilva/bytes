package io.github.andrebeat.bytes

import java.nio.{ByteBuffer, ByteOrder}
import sun.misc.Unsafe

trait Bytes {
  def size: Int

  def readByte(offset: Int): Byte
  def readShort(offset: Int): Short
  def readChar(offset: Int): Char
  def readInt(offset: Int): Int
  def readFloat(offset: Int): Float
  def readLong(offset: Int): Long
  def readDouble(offset: Int): Double

  def writeByte(offset: Int, value: Byte): Unit
  def writeShort(offset: Int, value: Short): Unit
  def writeChar(offset: Int, value: Char): Unit
  def writeInt(offset: Int, value: Int): Unit
  def writeFloat(offset: Int, value: Float): Unit
  def writeLong(offset: Int, value: Long): Unit
  def writeDouble(offset: Int, value: Double): Unit
}

trait ByteBufferBytes extends Bytes {
  protected[this] def bb: ByteBuffer
  def size = bb.limit()

  def readByte(offset: Int) = bb.get(offset)
  def readShort(offset: Int) = bb.getShort(offset)
  def readChar(offset: Int) = bb.getChar(offset)
  def readInt(offset: Int) = bb.getInt(offset)
  def readFloat(offset: Int) = bb.getFloat(offset)
  def readLong(offset: Int) = bb.getLong(offset)
  def readDouble(offset: Int) = bb.getDouble(offset)

  def writeByte(offset: Int, value: Byte) = bb.put(offset, value)
  def writeShort(offset: Int, value: Short) = bb.putShort(offset, value)
  def writeChar(offset: Int, value: Char) = bb.putChar(offset, value)
  def writeInt(offset: Int, value: Int) = bb.putInt(offset, value)
  def writeFloat(offset: Int, value: Float) = bb.putFloat(offset, value)
  def writeLong(offset: Int, value: Long) = bb.putLong(offset, value)
  def writeDouble(offset: Int, value: Double) = bb.putDouble(offset, value)
}

final class HeapBytes(protected[this] val bb: ByteBuffer) extends ByteBufferBytes
final class DirectBytes(protected[this] val bb: ByteBuffer) extends ByteBufferBytes

object HeapBytes {
  def apply(size: Int): HeapBytes = {
    val bb = ByteBuffer.allocate(size)
    bb.order(ByteOrder.nativeOrder)
    new HeapBytes(bb)
  }
}

object DirectBytes {
  def apply(size: Int): DirectBytes = {
    val bb = ByteBuffer.allocateDirect(size)
    bb.order(ByteOrder.nativeOrder)
    new DirectBytes(bb)
  }
}

final class UnsafeBytes(
  private[this] val baseAddr: Long,
  val size: Int)
    extends Bytes {

  def readByte(offset: Int) = UnsafeBytes.unsafe.getByte(baseAddr + offset)
  def readShort(offset: Int) = UnsafeBytes.unsafe.getShort(baseAddr + offset)
  def readChar(offset: Int) = UnsafeBytes.unsafe.getChar(baseAddr + offset)
  def readInt(offset: Int) = UnsafeBytes.unsafe.getInt(baseAddr + offset)
  def readFloat(offset: Int) = UnsafeBytes.unsafe.getFloat(baseAddr + offset)
  def readLong(offset: Int) = UnsafeBytes.unsafe.getLong(baseAddr + offset)
  def readDouble(offset: Int) = UnsafeBytes.unsafe.getDouble(baseAddr + offset)

  def writeByte(offset: Int, value: Byte) = UnsafeBytes.unsafe.putByte(baseAddr + offset, value)
  def writeShort(offset: Int, value: Short) = UnsafeBytes.unsafe.putShort(baseAddr + offset, value)
  def writeChar(offset: Int, value: Char) = UnsafeBytes.unsafe.putChar(baseAddr + offset, value)
  def writeInt(offset: Int, value: Int) = UnsafeBytes.unsafe.putInt(baseAddr + offset, value)
  def writeFloat(offset: Int, value: Float) = UnsafeBytes.unsafe.putFloat(baseAddr + offset, value)
  def writeLong(offset: Int, value: Long) = UnsafeBytes.unsafe.putLong(baseAddr + offset, value)
  def writeDouble(offset: Int, value: Double) = UnsafeBytes.unsafe.putDouble(baseAddr + offset, value)
}

object UnsafeBytes {
  private val unsafe: Unsafe = {
    val theUnsafe = classOf[Unsafe].getDeclaredField("theUnsafe")
    theUnsafe.setAccessible(true)
    theUnsafe.get(null).asInstanceOf[Unsafe]
  }

  def apply(size: Int): UnsafeBytes =
    new UnsafeBytes(unsafe.allocateMemory(size), size)
}
