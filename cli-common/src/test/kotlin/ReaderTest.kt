import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.EOFException
import java.io.PrintStream
import model.Point2D
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ReaderTest {
    @Test
    fun `readLinearRing retries invalid geometry and returns the next valid ring`() {
        val ring = withConsoleInput(
            "0 0, 1 1, 2 2\n" +
                "0 0, 3 0, 0 4\n"
        ) {
            readLinearRing("Ring: ")
        }

        assertEquals(
            listOf(Point2D(0.0, 0.0), Point2D(3.0, 0.0), Point2D(0.0, 4.0)),
            ring.vertices
        )
    }

    @Test
    fun `readLinearRing propagates EOF after invalid geometry`() {
        assertFailsWith<EOFException> {
            withConsoleInput("0 0, 1 1, 2 2\n") {
                readLinearRing("Ring: ")
            }
        }
    }

    private fun <T> withConsoleInput(input: String, action: () -> T): T {
        val originalInput = System.`in`
        val originalOutput = System.out

        try {
            System.setIn(ByteArrayInputStream(input.toByteArray()))
            System.setOut(PrintStream(ByteArrayOutputStream()))
            return action()
        } finally {
            System.setIn(originalInput)
            System.setOut(originalOutput)
        }
    }
}
