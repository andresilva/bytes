package io.github.andrebeat.bytes.benchmark

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.{Input, Output}
import com.google.caliper.{Param, SimpleBenchmark}
import io.github.andrebeat.bytes._
import java.io._
import org.objenesis.strategy.StdInstantiatorStrategy
import scala.util.Random

case class Foo(
  b: Byte,
  s: Short,
  c: Char,
  i: Int,
  f: Float,
  l: Long,
  d: Double)

class ReadBenchmark extends SimpleBenchmark {
  val foo = Foo(1, 42, '?', 13231, 9023.0f, 55554434L, 321.0)
  val bytes: Bytes = UnsafeBytes(1024)
  Write(bytes, 0, foo)

  // Java Serialization
  val javaBaos = new ByteArrayOutputStream(1024)
  val javaOos = new ObjectOutputStream(javaBaos)
  javaOos.writeObject(foo)
  javaOos.close()
  val javaByteArray = javaBaos.toByteArray
  val javaBais = new ByteArrayInputStream(javaByteArray)
  var javaOis = new ObjectInputStream(javaBais)

  // Kryo Serialization
  val kryo = new Kryo()
  kryo.setRegistrationRequired(false)
  kryo.setInstantiatorStrategy(new StdInstantiatorStrategy())
  val kryoBaos = new ByteArrayOutputStream(1024)
  val kryoOos = new Output(kryoBaos)
  kryo.writeObject(kryoOos, foo)
  kryoOos.close()
  val kryoByteArray = kryoBaos.toByteArray
  val kryoBais = new ByteArrayInputStream(kryoByteArray)
  var kryoOis = new Input(kryoBais)

  def timeBytes(reps: Int) = {
    var i = 0
    var foo: Foo = null
    while (i < reps) {
      foo = Read[Foo](bytes, 0)._1
      i += 1
    }
    foo
  }

  def timeJava(reps: Int) = {
    var i = 0
    var foo: Foo = null

    while (i < reps) {
      javaBais.reset()
      javaOis = new ObjectInputStream(javaBais)
      foo = javaOis.readObject.asInstanceOf[Foo]
      javaOis.close()
      i += 1
    }
    foo
  }

  def timeKryo(reps: Int) = {
    var i = 0
    var foo: Foo = null

    while (i < reps) {
      kryoBais.reset()
      kryoOis = new Input(kryoBais)
      foo = kryo.readObject(kryoOis, classOf[Foo])
      kryoOis.close()
      i += 1
    }
    foo
  }
}
