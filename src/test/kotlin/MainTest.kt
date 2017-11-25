/**
 * Created by eginez on 11/11/17.
 */
import org.junit.Test
import java.io.StringReader

class MainTest {
    var env =  Environment(InitEnv as MutableMap<String, Any>)

    @Test
    fun testSimple(){
        val s = StringReader("(+ 1 2 3 4 5)")
        val n = readNode(s)
        println(n.eval(env))
    }

    @Test
    fun testSimpleExpression() {
        val r = StringReader("(+ 2 4)")
        val n = readNode(r)
        assert(n.eval(env) == 6)
    }

    @Test
    fun testSimpleExpression2() {
        val r = StringReader("(+ 1 (+ 2 4))")
        val n = readNode(r)
        assert(n.eval(env) == 7)
    }

    @Test
    fun testSimpleExpression3() {
        val r = StringReader("(define c 3)")
        val n = readNode(r)
        n.eval(env)
        assert(env.containsKey("c"))
    }

    @Test
    fun testSimpleDefn() {
        val r = StringReader("(fn (x) (+ x 10))")
        val n = readNode(r)
        val fnNode = n.eval(env)
        assert(fnNode is FunctionNode)
        val fn = fnNode as FunctionNode
        val res = fn.apply(listOf(NumberNode(10)), env)
        assert(res == 20)

    }

    @Test
    fun test2Expressions() {
        val expr = """ (define x 10)
            (+ x 10)
            """
        val r = StringReader(expr)
        val n = readListNode(r)
        val res = evalAllNodes(n.nodes, env)
        assert(res == 20)
    }

    @Test
    fun testFibo() {
        val expr = """
(define sum (fn (x)
                (+ x 10)))
(sum 2)
            """
        val r = StringReader(expr)
        val n = readListNode(r)
        val res = evalAllNodes(n.nodes, env)
        assert(res == 12)
    }
}