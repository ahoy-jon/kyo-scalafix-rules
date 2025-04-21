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

    def defers(term: Stat): Seq[Term] =
      term match {
        case d@Term.Apply.After_4_6_0(Term.Name("defer"), _) => Seq(d)
        case Term.Block(stats) =>
          stats.flatMap(defers)
        case _ => Nil
      }

    def patchDefer(t: Term): Patch =
      Patch.addLeft(t, "(") + Patch.addRight(t, ").unit")

    def isUnit(t: Type): Boolean = {
      //check subtype ?
      t match {
        case Type.Name("Unit") => true
        case Type.ApplyInfix(Type.Name("Unit"), Type.Name("<"), _) => true
        case _ => false
      }
    }

    doc.tree.collect({
      case Defn.Val(_, _, Some(t), rhs) if isUnit(t) =>
        defers(rhs).map(patchDefer).asPatch
      case Defn.Def.After_4_7_3(_, _, _, Some(t), rhs) if isUnit(t) =>
        defers(rhs).map(patchDefer).asPatch

    }).asPatch
  }

}
