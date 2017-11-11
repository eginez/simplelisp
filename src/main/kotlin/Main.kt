import java.io.*

/*
Node = NumberNode | ListNode

*/

val ValidFunctions = listOf('+', '-')
val EmptyNode = ListNode(emptyList())
val InvalidSyntax = RuntimeException("Invalid Syntax")

abstract class Node {
    abstract fun eval(): Any
}


inline fun whileNotEOF(input: Reader, body: (Char)->Unit) {
    var value = input.read()
    while (value != -1) {
        body(value.toChar())
        value = input.read()
    }

}
abstract class FunctionNode(val name: String): Node() {
    override fun eval(): Any = this
    abstract fun apply(operands: List<Node>): Any
}

class ListNode(val nodes: List<Node>) : Node() {
    override fun eval(): Any {
        val op = nodes[0] as FunctionNode
        return op.apply(nodes.drop(1))
    }
}

class NumberNode(val number: Number): Node() {
    override fun eval(): Any = this.number
}


fun readNode(input: Reader): Node {
    whileNotEOF(input) { c ->
        when (c) {
            '(' -> return readListNode(PushbackReader(input))
            in '0'..'9' -> return readNumberNode(PushbackReader(input))
            in ValidFunctions -> return readFunctionNode(input)
            ' ' -> {}
            ')' -> throw InvalidSyntax
        }
    }
    return EmptyNode
}

fun readFunctionNode(input: Reader): FunctionNode {
    whileNotEOF(input) { c ->
        when(c) {
            '+' -> return object : FunctionNode("PLUS"){
                override fun apply(operands: List<Node>): Any {
                    var sum = 0
                    operands.forEach { sum += it.eval() as Int }
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
            ' ' -> {}
        }
    }
    throw InvalidSyntax
}

fun main(args: Array<String>) {
    val buffReader = BufferedReader(InputStreamReader(System.`in`))
}