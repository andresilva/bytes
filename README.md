bytes
=====

A playground for fast, typesafe and boilerplate-free object serialization.

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
  d: Double)

val foo = Foo(1, 42, '?', 13231, 9023.0f, 55554434L, 321.0)
// foo: Foo = Foo(1,42,?,13231,9023.0,55554434,321.0)

val bytes: Bytes = UnsafeBytes(1024)
// bytes: io.github.andrebeat.bytes.Bytes = io.github.andrebeat.bytes.UnsafeBytes@6efa304f

Write(bytes, 0, foo)
// res0: Int = 29

val (foo2, size) = Read[Foo](bytes, 0)
// foo2: Foo = Foo(1,42,?,13231,9023.0,55554434,321.0)
// size: Int = 29
```
