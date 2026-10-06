import model.Point2D
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class Point2DTest {

    @Test
    fun `Should reject invalid values`() {
        val invalidValues = listOf(
            Double.NaN,
            Double.POSITIVE_INFINITY,
            Double.NEGATIVE_INFINITY
        )

        for (value in invalidValues) {
            assertFailsWith<IllegalArgumentException> { Point2D(value, 0.0) }
            assertFailsWith<IllegalArgumentException> { Point2D(0.0, value) }
        }
    }
}