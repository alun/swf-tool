import apparat.swf._
import apparat.abc._
import apparat.bytecode._
import apparat.utils._
import combinator._
import BytecodeChains._
import operations._
import apparat.abc.analysis._

object Tool {

  import apparat.abc.AbcNamespaceKind._
  val proxyPackage = AbcNamespace(Package,Symbol("com.oggi.Player"))
  val proxyClass = AbcQName('ListenerContext, proxyPackage)
  val rootPackage = AbcNamespace(Package,Symbol(""))
  val proxyMethod = AbcQName('bind, rootPackage)

  def main(args: Array[String]) {
    for {
      file <- args.headOption
    } {
      args.drop(1).headOption match {
        case Some("proxy") =>
          // we get a "proxy" instuction so lets change addEventListener calls to proxy
          proxify(file)
        case _ =>
          dump(file)
      }
    }
  }

  def dump(file: String) {
    val swf = Swf fromFile file
    for {
      tag <- swf
      abc <- Abc fromTag tag
    } {
      abc.loadBytecode()
      for {
        method <- abc.methods
      } {
        method.dump()
      }
    }
  }

  def proxify(file: String) {
      SwfTags.tagFactory = (kind: Int) => kind match {
        case SwfTags.DoABC => Some(new DoABC)
        case SwfTags.DoABC1 => Some(new DoABC)
        case _ => None
      }

      val cont = TagContainer fromFile file
      cont foreachTagSync proxify
      cont write file
  }


  private def proxify: PartialFunction[SwfTag, Unit] = {
    case doABC: DoABC =>
      val abc = Abc fromDoABC doABC

      abc.loadBytecode()
      var replaced = false

      for {
        method <- abc.methods
        body <- method.body
        bytecode <- body.bytecode
      } {
        var flag = true
        while (flag) {
          flag = replaceAddEventListener(bytecode)
          if (flag) {
            replaced = true
          }
        }
        if (replaced) {
          body.maxStack += 1
        }
      }

      if (replaced) {
        abc.cpool = AbcConstantPoolBuilder using abc
        abc.saveBytecode()
        abc write doABC
      }
  }

  /**
   * This method can be used to block stage access from 3rd part libraries (such as GoogleMap)
   * by implementing a handler which will maintain a real addEventListener logic
   */
  private def replaceAddEventListener(bytecode: Bytecode) = {
    val ops = bytecode.ops
    
    // try to see if bytecode has addEventListener call operation
    ops.find {
      case CallProperty(AbcQName('addEventListener, _), _) => true
      case CallPropVoid(AbcQName('addEventListener, _), _) => true
      case _ => false
    } .map { addEventListenerOp =>
      // if so let replace it to call a proxy instead
      val idx = ops.indexOf(addEventListenerOp)
      val (beforeOps, _) = ops.splitAt(idx)
      val (_, paramOps) = beforeOps.reverse.foldLeft((addEventListenerOp.popOperands, List.empty[AbstractOp])) {
        // harvest previous Operations which was used to fill the stack with call params
        case ((stackSize, ops), op) if stackSize > 0 =>
          (stackSize - op.pushOperands + op.popOperands, op :: ops)
        // all operations are harvested, so skip to the end of the list
        case ((stackSize, ops), _) => (stackSize, ops)
      }
      // now we need to build a chain which we want to replace
      val chain = (paramOps :+ addEventListenerOp).foldLeft[BytecodeChain[_]](null) {
        case (chain, firstOp) if chain == null => filter { case `firstOp` => true }
        case (chain, anotherOp) => chain ~ filter { case `anotherOp` => true }
      }
      val newChain = FindPropStrict(proxyClass) ::
        GetProperty(proxyClass) ::
        paramOps :::
        CallProperty(proxyMethod, addEventListenerOp.popOperands) :: 
        (if (addEventListenerOp.opCode == Op.callpropvoid) Pop() :: Nil else Nil)

      // replace the chain with proxy call chain
      val result = bytecode.replace(chain)(_ => newChain)
      newChain.filter(bytecode.markers.hasMarkerFor _) match {
        case op :: rest => bytecode.markers.forwardMarker(op, newChain(0))
        case _ =>
      }
      result
    } .getOrElse(false)
  }

}
