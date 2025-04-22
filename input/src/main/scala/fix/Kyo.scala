/*
rule = Kyo
*/
package fix

import kyo.*

object Kyo {
  val x: Unit < Any = defer {
    Console.printLine("warn").now
  }

  val y: Unit < IO = defer {
    Console.printLine("ok").now
  }

  val z: Unit < Any = {
    defer {
      Console.printLine("nok1").now
    }
    defer {
      Console.printLine("nok2").now
    }
  }

  val xx: Unit < Any = defer {
    Console.printLine("warn").now
  }

  def oups(i:Int): Unit < Any = defer {
    Console.printLine("a" * i).now
  }

  def oupsOups(i:Int): Unit < Any = {
    defer {
      Console.printLine("nok1").now
    }
    defer {
      Console.printLine("nok2").now
    }
  }

  type A = Unit < Any

  val a:A = defer (
    Console.printLine("warn").now
  )

  type U = Unit
  type B = U < Any
  val b: B = defer {
    Console.printLine("warn").now
  }

  val c: U < Any = defer {
    Console.printLine("warn").now
  }

}