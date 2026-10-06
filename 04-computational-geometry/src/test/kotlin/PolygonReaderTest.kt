import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.EOFException
import java.io.PrintStream
import model.Point2D
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class PolygonReaderTest {
    @Test
    fun `readPolygon retries an invalid hole and returns the next valid polygon`() {
        val polygon = withConsoleInput(
            "0 0, 10 0, 10 10, 0 10\n" +
                "y\n" +
                "1\n" +
                "12 12, 14 12, 14 14, 12 14\n" +
                "0 0, 10 0, 10 10, 0 10\n" +
                "n\n"
        ) {
            readPolygon()
        }

        assertEquals(emptyList(), polygon.interiorRings)
        assertEquals(
            listOf(Point2D(0.0, 0.0), Point2D(10.0, 0.0), Point2D(10.0, 10.0), Point2D(0.0, 10.0)),
            polygon.exteriorRing.vertices
        )
    }

    @Test
    fun `readPolygon propagates EOF after an invalid hole`() {
        assertFailsWith<EOFException> {
            withConsoleInput(
                "0 0, 10 0, 10 10, 0 10\n" +
                    "y\n" +
                    "1\n" +
                    "12 12, 14 12, 14 14, 12 14\n"
            ) {
                readPolygon()
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
