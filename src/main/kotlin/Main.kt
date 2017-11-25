import java.io.*


val ValidFunctions = listOf('+', '-')
val EmptyNode = ListNode(emptyList())
val InvalidSyntax = RuntimeException("Invalid Syntax")


class Environment(val map:MutableMap<String, Any>) : MutableMap<String, Any>  by map

sealed class Node {
    abstract fun eval(env: Environment): Any
}


inline fun whileNotEOF(input: Reader, body: (Char)->Unit) {
    var value = input.read()
    while (value != -1) {
        body(value.toChar())
        value = input.read()
    }

}
abstract class FunctionNode(val name: String): Node() {
    override fun eval(env: Environment): Any = this
    abstract fun apply(operands: List<Node>, env: Environment): Any
}

class SymbolNode(val name: String): Node() {
    override fun eval(env: Environment): Any = env.getOrDefault(name, EmptyNode)
}

class ListNode(val nodes: List<Node>) : Node() {
    override fun eval(env: Environment): Any {
        val op = nodes[0]
        val symbolListClosure = { opr: Node ->
            val newList = mutableListOf(opr.eval(env) as Node)
            newList += nodes.drop(1)
            ListNode(newList).eval(env)
        }
        return when(op) {
            is FunctionNode -> op.apply(nodes.drop(1), env)
            is NumberNode -> op.eval(env)
            is SymbolNode  -> symbolListClosure(op)
            is ListNode -> symbolListClosure(op)
        }
    }
}

val DefineSymbol = object: FunctionNode("define") {
    override fun apply(operands: List<Node>, env: Environment): Any {
        assert(operands.size == 2)
        val symbol = operands[0] as SymbolNode
        env.put(symbol.name, operands[1])
        return symbol
    }
}

val DefineFunction = object: FunctionNode("fn") {
    override fun apply(operands: List<Node>, env: Environment): Any {
        val args = operands[0]
        val body = operands[1]
        val fn = object: FunctionNode("") {
            override fun apply(operands: List<Node>, env: Environment): Any {
                assert(args is ListNode)

                val fnEnv = Environment(env)
                val formalParams = args as ListNode
                formalParams.nodes.map { it as SymbolNode }
                        .forEachIndexed { i, symbol -> fnEnv[symbol.name] = operands[i] }
                return body.eval(fnEnv)
            }
        }
        return fn
    }
}



class NumberNode(val number: Number): Node() {
    override fun eval(env: Environment): Any = this.number
}


//Recursively evaluates nodes until a primitive type is found
fun fullyEval(node: Any, env: Environment): Any {
    return if (node is Node && node != EmptyNode) {
        fullyEval(node.eval(env), env)
    } else {
        node
    }
}

fun readNode(input: Reader): Node {
    whileNotEOF(input) { c ->
        when (c) {
            '(' -> return readListNode(PushbackReader(input))
            in '0'..'9' -> return readNumberNode(PushbackReader(input))
            in 'a'..'z' ->  return readSymbolNode(PushbackReader(input))
            in ValidFunctions -> return readFunctionNode(input)
            in listOf(' ', '\n') -> {}
            ')' -> throw InvalidSyntax
        }
    }
    return EmptyNode
}


fun readFunctionNode(input: Reader): FunctionNode {
    whileNotEOF(input) { c ->
        when(c) {
            '+' -> return object : FunctionNode("PLUS"){
                override fun apply(operands: List<Node>, env: Environment): Any {
                    var sum = 0
                    operands.forEach { sum += fullyEval(it, env) as Int }
                    return sum
                }
            }

            else -> throw InvalidSyntax
        }
    }
    throw InvalidSyntax
}

fun readNumberNode(input: PushbackReader): NumberNode {
    val num = StringBuilder()
    whileNotEOF(input) { c ->
        when(c) {
            in '0'..'9' -> num.append(c)
            else -> {
                input.unread(c.toInt())
                return NumberNode(num.toString().toInt())
            }
        }
    }
    throw InvalidSyntax
}

fun readSymbolNode(input: PushbackReader): Node {
    val symbol = StringBuilder()
    whileNotEOF(input) { c ->
        when(c) {
            in 'a'..'z' -> symbol.append(c)
            else -> {
                input.unread(c.toInt())
                return SymbolNode(symbol.toString())
            }
        }
    }
    throw InvalidSyntax
}

fun readListNode(input: PushbackReader): ListNode {
    val allNodes = mutableListOf<Node>()
    whileNotEOF(input) { c ->
        when(c) {
            '(' -> allNodes.add(readListNode(input))
            ')' -> return ListNode(allNodes)
            in ValidFunctions ->  {
                input.unread(c.toInt())
                allNodes.add(readFunctionNode(input))
            }
            in '0'..'9' -> {
                input.unread(c.toInt())
                allNodes.add(readNumberNode(input))
            }
            in 'a'..'z' -> {
                input.unread(c.toInt())
                allNodes.add(readSymbolNode(input))
            }
            ' ' -> {}
        }
    }
    return ListNode(allNodes)
}

fun readListNode(input: Reader): ListNode = readListNode(PushbackReader(input))

fun evalAllNodes(nodes: List<Node>, env: Environment): Any? {
    var out: Any? = null
    nodes.forEach { out = it.eval(env) }
    return out
}


val InitEnv = mutableMapOf(
        DefineSymbol.name to DefineSymbol,
        DefineFunction.name to DefineFunction)

fun main(args: Array<String>) {
    val buffReader = BufferedReader(InputStreamReader(System.`in`))
}