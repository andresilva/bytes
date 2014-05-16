package io.github.andrebeat.bytes.benchmark

import com.google.caliper.{Param, SimpleBenchmark}
import io.github.andrebeat.bytes._
import scala.util.Random

class BytesBenchmark extends SimpleBenchmark {
  @Param(Array("256", "512", "1024", "2048", "4096"))
  val size: Int = 0

  @Param(Array("Heap", "Direct", "Unsafe"))
  val bytesType: String = ""

  var bytes: Bytes = _

  override def setUp() {
    bytes = bytesType match {
      case "Heap" => HeapBytes(size)
      case "Direct" => DirectBytes(size)
      case "Unsafe" => UnsafeBytes(size)
    }
  }

  def timeReadInt(reps: Int) = {
    var i = 0
    var result = 0
    while (i < reps) {
      var offset = 0
      while (offset < bytes.size) {
        result = bytes.readInt(offset)
        offset += 4
      }
      i += 1
    }
    result
  }

  def timeReadLong(reps: Int) = {
    var i = 0
    var result = 0L
    while (i < reps) {
      var offset = 0
      while (offset < bytes.size) {
        result = bytes.readLong(offset)
        offset += 8
      }
      i += 1
    }
    result
  }

  override def tearDown() {
  }
}
