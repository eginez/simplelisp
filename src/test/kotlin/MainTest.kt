/**
 * Created by eginez on 11/11/17.
 */
import org.junit.Test
import java.io.StringReader

class MainTest {

    @Test
    fun testSimpleExpression() {
        val r = StringReader("(+ 2 4)")
        val n = readNode(r)
        assert(n.eval() == 6)
    }

    @Test
    fun testSimpleExpression2() {
        val r = StringReader("(+ 1 (+ 2 4))")
        val n = readNode(r)
        assert(n.eval() == 7)
    }
}