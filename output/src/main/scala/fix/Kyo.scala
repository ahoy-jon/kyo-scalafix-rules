package fix

import kyo.*

object Kyo {
  val x: Any < Any = defer {
    Console.printLine("warn").now
  }

  val y: Any < IO = defer {
    Console.printLine("ok").now
  }
  
  val z: Any < Any = {
    defer {
      Console.printLine("nok1").now
    }
    defer {
      Console.printLine("nok2").now
    }
  }

  val xx: Any < Any = defer {
    Console.printLine("warn").now
  }
  
  def oups(i:Int): Any < Any = defer {
    Console.printLine("a" * i).now
  }
  
  def oupsOups(i:Int): Any < Any = {
    defer {
      Console.printLine("nok1").now
    }
    defer {
      Console.printLine("nok2").now
    }
  }


}