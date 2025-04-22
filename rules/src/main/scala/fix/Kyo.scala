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

    def upperBound(symbol: Symbol): Option[SemanticType] = {
      symbol.info match {
        case Some(symbolInformation) =>
          symbolInformation.signature match {
            //case ValueSignature(tpe) => Some(tpe)
            case TypeSignature(_, _, upperBound) => Some(upperBound)
            case _ => None
          }
        case _ => None
      }
    }

    def isSemanticUnit(semanticType: SemanticType): Boolean = {
      semanticType match {
        //Unit
        case TypeRef(NoType, UnitType, Nil) => true
        //a < _
        case TypeRef(NoType, PendingType, List(a, _)) => isSemanticUnit(a)
        //alias
        case TypeRef(NoType, symbol, Nil) => upperBound(symbol).exists(isSemanticUnit)
        case _ => false
      }
    }

    def isUnitType(t: Type): Boolean = {
      def fromSyntax: Boolean = t match {
        case Type.Name("Unit") => true
        case Type.ApplyInfix(t, Type.Name("<"), _) => isUnitType(t)
        case _ => false
      }

      def fromSymbol: Boolean = upperBound(t.symbol).exists(isSemanticUnit)

      fromSyntax || fromSymbol
    }

    doc.tree.collect({
      case Defn.Val(_, _, Some(t), rhs) if isUnitType(t) =>
        defers(rhs).map(patchDefer).asPatch
      case Defn.Def.After_4_7_3(_, _, _, Some(t), rhs) if isUnitType(t) =>
        defers(rhs).map(patchDefer).asPatch
    }).asPatch
  }

}
