package fix

import kyo.*

object Kyo {
  val x: Unit < Any = (defer {
    Console.printLine("warn").now
  }).unit

  val y: Unit < IO = (defer {
    Console.printLine("ok").now
  }).unit

  val z: Unit < Any = {
    (defer {
      Console.printLine("nok1").now
    }).unit
    (defer {
      Console.printLine("nok2").now
    }).unit
  }

  val xx: Unit < Any = (defer {
    Console.printLine("warn").now
  }).unit

  def oups(i:Int): Unit < Any = (defer {
    Console.printLine("a" * i).now
  }).unit

  def oupsOups(i:Int): Unit < Any = {
    (defer {
      Console.printLine("nok1").now
    }).unit
    (defer {
      Console.printLine("nok2").now
    }).unit
  }

  type A = Unit < Any

  val a:A = (defer (
    Console.printLine("warn").now
  )).unit

  type U = Unit
  type B = U < Any
  val b: B = (defer {
    Console.printLine("warn").now
  }).unit

  val c: U < Any = (defer {
    Console.printLine("warn").now
  }).unit

}