package fix

import scalafix.v1._

import scala.meta._


class Kyo extends SemanticRule("Kyo") {

  override def fix(implicit doc: SemanticDocument): Patch = {
    //println("Tree.syntax: " + doc.tree.syntax)
    //println("Tree.structure: " + doc.tree.structure)
    //println("Tree.structureLabeled: " + doc.tree.structureLabeled)


    def defers(term: Stat): Seq[Term] =
      term match {
        case d@Term.Apply.After_4_6_0(Term.Name("defer"), _) => Seq(d)
        case Term.Block(stats) =>
          stats.flatMap(defers)
        case _ => Nil
      }

    def patchDefer(t: Term): Patch = {
      Patch.addLeft(t, "(") + Patch.addRight(t, ").unit")
    }

    //TODO : detect lifted defers

    val PendingType: Symbol = Symbol("kyo/kernel/Pending$package.`<`#")
    val UnitType: Symbol = Symbol("scala/Unit#")

    def isSemanticUnit(semanticType: SemanticType): Boolean = {
      semanticType match {
        case TypeRef(NoType, PendingType, List(a, _)) =>
          isSemanticUnit(a)
        case TypeRef(NoType, UnitType, Nil) => true
        case _ => false
      }
    }

    def isUnitType(t: Type): Boolean = {
      //check subtype ?
      val fromSyntax: Boolean = t match {
        case Type.Name("Unit") => true
        case Type.ApplyInfix(Type.Name("Unit"), Type.Name("<"), _) => true
        case _ => false
      }
      //println("" + t + ":"+ t.structure + ":" +   t.symbol.info)
      t.symbol.info match {
        case _ if fromSyntax => true
        case Some(value) =>
          value.signature match {
            case TypeSignature(Nil, _, up) => isSemanticUnit(up)
            case _ => false
          }
        case None => false
      }
    }

    doc.tree.collect({
      case Defn.Val(_, _, Some(t), rhs) if isUnitType(t) =>
        defers(rhs).map(patchDefer).asPatch
      case Defn.Def.After_4_7_3(_, _, _, Some(t), rhs) if isUnitType(t) =>
        defers(rhs).map(patchDefer).asPatch
    }).asPatch
  }

}
