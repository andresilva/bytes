bytes
=====

A playground for fast, typesafe and boilerplate-free object serialization in Scala.

This is what we've got so far:

```scala
import io.github.andrebeat.bytes._

case class Foo(
  b: Byte,
  s: Short,
  c: Char,
  i: Int,
  f: Float,
  l: Long,
  d: Double,
  is: List[Int],
  set: Set[Char],
  map: Map[Long, Int],
  opt: Option[Float],
  either: Either[Byte, Int])

val foo = Foo(
    1, 42, '?', 13231, 9023.0f, 55554434L, 321.0,
    1 :: 2 :: 3 :: Nil,
    Set('a', 'b', 'c'),
    Map(99L -> 0, 98L -> 1),
    None,
    Right(4))

val bytes: Bytes = UnsafeBytes(1024)

Write(bytes, 0, foo)
// res0: Int = 83

val (foo2, size) = Read[Foo](bytes, 0)
// foo2: Foo = Foo(1,42,?,13231,9023.0,55554434,321.0,List(1, 2, 3),Set(a, b, c),Map(99 -> 0, 98 -> 1),None,Right(4))
// size: Int = 83
```
