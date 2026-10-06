import kotlinx.collections.immutable.persistentListOf
import model.LinearRing2D
import model.Point2D
import model.Segment2D
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class LinearRing2DTest {

    @Test
    fun `Should reject fewer than three vertices`() {
        assertFailsWith<IllegalArgumentException> {
            LinearRing2D(
                persistentListOf(
                    Point2D(0.0, 0.0),
                    Point2D(1.0, 1.0),
                )
            )
        }
    }

    @Test
    fun `Should reject identical vertices`() {
        assertFailsWith<IllegalArgumentException> {
            LinearRing2D(
                persistentListOf(
                    Point2D(1.0, 1.0),
                    Point2D(1.0, 1.0),
                    Point2D(1.0, 1.0)
                )
            )
        }
    }

    @Test
    fun `Should reject collinear vertices`() {
        assertFailsWith<IllegalArgumentException> {
            LinearRing2D(
                persistentListOf(
                    Point2D(0.0, 0.0),
                    Point2D(1.0, 1.0),
                    Point2D(2.0, 2.0),
                )
            )
        }
    }

    @Test
    fun `Should accept a valid triangle`() {
        val vertices = persistentListOf(
            Point2D(0.0, 0.0),
            Point2D(1.0, 0.0),
            Point2D(0.0, 1.0)
        )

        val ring = LinearRing2D(vertices)

        assertEquals(vertices, ring.vertices)
    }

    @Test
    fun `Show reject crossing non-neighbouring edge`() {
        assertFailsWith<IllegalArgumentException> {
            LinearRing2D(
                persistentListOf(
                    Point2D(0.0, 0.0),
                    Point2D(2.0, 2.0),
                    Point2D(0.0, 2.0),
                    Point2D(2.0, 0.0)
                )
            )
        }
    }

    @Test
    fun `Should reject touching non-neighbouring edges`() {
        assertFailsWith<IllegalArgumentException> {
            LinearRing2D(
                persistentListOf(
                    Point2D(0.0, 0.0),
                    Point2D(4.0, 0.0),
                    Point2D(4.0, 4.0),
                    Point2D(2.0, 0.0),
                    Point2D(0.0, 4.0)
                )
            )
        }
    }

    @Test
    fun `Should detect overlapping segments`() {
        val firstSegment = Segment2D(
            Point2D(0.0, 0.0),
            Point2D(4.0, 0.0)
        )
        val secondSegment = Segment2D(
            Point2D(2.0, 0.0),
            Point2D(6.0, 0.0)
        )

        assertTrue(firstSegment.intersects(secondSegment))
    }

    @Test
    fun `Should reject identical consecutive vertices`() {
        assertFailsWith<IllegalArgumentException> {
            LinearRing2D(
                persistentListOf(
                    Point2D(0.0, 0.0),
                    Point2D(4.0, 0.0),
                    Point2D(4.0, 0.0),
                    Point2D(0.0, 4.0)
                )
            )
        }
    }

    @Test
    fun `Should reject neighbouring edges folded back and overlapped`() {
        assertFailsWith<IllegalArgumentException> {
            LinearRing2D(
                persistentListOf(
                    Point2D(0.0, 0.0),
                    Point2D(4.0, 0.0),
                    Point2D(2.0, 0.0),
                    Point2D(2.0, 4.0),
                    Point2D(0.0, 4.0)
                )
            )
        }
    }

    @Test
    fun `Should accept neighbouring edges continuing straight`() {
        val vertices = persistentListOf(
            Point2D(0.0, 0.0),
            Point2D(2.0, 0.0),
            Point2D(4.0, 0.0),
            Point2D(4.0, 4.0),
            Point2D(0.0, 4.0)
        )

        val ring = LinearRing2D(vertices)

        assertEquals(vertices, ring.vertices)
    }
}