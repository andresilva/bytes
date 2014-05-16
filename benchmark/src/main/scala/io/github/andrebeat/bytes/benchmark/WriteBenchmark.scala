package io.github.andrebeat.bytes.benchmark

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.{Input, Output}
import com.google.caliper.{Param, SimpleBenchmark}
import io.github.andrebeat.bytes._
import java.io._
import org.objenesis.strategy.StdInstantiatorStrategy
import scala.util.Random

class WriteBenchmark extends SimpleBenchmark {
  val foo = Foo(1, 42, '?', 13231, 9023.0f, 55554434L, 321.0)
  val bytes: Bytes = HeapBytes(1024)

  // Java Serialization
  var javaBaos = new ByteArrayOutputStream(100)
  var javaOos = new ObjectOutputStream(javaBaos)

  // Kryo Serialization
  val kryo = new Kryo()
  kryo.setRegistrationRequired(false)
  kryo.setInstantiatorStrategy(new StdInstantiatorStrategy())
  var kryoBaos = new ByteArrayOutputStream(100)
  var kryoOos = new Output(kryoBaos)

  def timeBytes(reps: Int) = {
    var i = 0
    while (i < reps) {
      Write(bytes, 0, foo)
      i += 1
    }
  }

  def timeJava(reps: Int) = {
    var i = 0
    while (i < reps) {
      javaBaos = new ByteArrayOutputStream(100)
      javaOos = new ObjectOutputStream(javaBaos)
      javaOos.writeObject(foo)
      javaOos.close()

      i += 1
    }
  }

  def timeKryo(reps: Int) = {
    var i = 0
    while (i < reps) {
      kryoBaos = new ByteArrayOutputStream(100)
      kryoOos = new Output(kryoBaos)
      kryo.writeObject(kryoOos, foo)
      kryoOos.close()

      i += 1
    }
  }
}
