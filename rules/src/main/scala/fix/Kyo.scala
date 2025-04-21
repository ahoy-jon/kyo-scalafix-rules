package fix

import scalafix.v1._

import scala.annotation.tailrec
import scala.meta._


class Kyo extends SemanticRule("Kyo") {

  override def fix(implicit doc: SemanticDocument): Patch = {
    //println("Tree.syntax: " + doc.tree.syntax)
    //println("Tree.structure: " + doc.tree.structure)
    //println("Tree.structureLabeled: " + doc.tree.structureLabeled)


    //println("-" * 20)
    doc.tree.traverse({
      case s: Defn.Val =>
        val synthetics: List[SemanticTree] = s.rhs.synthetics
        val structure = synthetics.structure
        //println(s.symbol)
        //println(structure)
        //println("-" * 20)

    })

    @tailrec
    def endsWithDefer(term: Stat): Boolean =
      term match {
        case Term.Apply.After_4_6_0(Term.Name("defer"), _) => true
        case Term.Block(stats) =>
          stats.lastOption match {
            case Some(value) => endsWithDefer(value)
            case None => false
          }
        case _ => false
      }


    doc.tree.collect({
      case Defn.Val(_, _, Some(Type.ApplyInfix(unit@Type.Name("Unit"), Type.Name("<"), context)), rhs)
        if endsWithDefer(rhs) =>
        Patch.replaceTree(unit, "Any")
      case Defn.Def.After_4_7_3(_, _, _, Some(Type.ApplyInfix(unit@Type.Name("Unit"), Type.Name("<"), context)), rhs)
        if endsWithDefer(rhs) =>
        Patch.replaceTree(unit, "Any")
    }).asPatch
  }

}
