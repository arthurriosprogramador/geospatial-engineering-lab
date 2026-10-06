import model.Point2D
import model.Segment2D
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class Segment2DTest {

    @Test
    fun `Should accept point on segment`() {
        val segment = Segment2D(
            start = Point2D(0.0, 0.0),
            end = Point2D(1.0, 0.0)
        )

        val pointOnSegment = Point2D(0.5, 0.9e-6)

        assertTrue(segment.isPointOnSegment(pointOnSegment))
    }

    @Test
    fun `Should reject point off segment`() {
        val segment = Segment2D(
            start = Point2D(0.0, 0.0),
            end = Point2D(1.0, 0.0)
        )

        val pointOffSegment = Point2D(0.5, 1.1e-6)

        assertFalse(segment.isPointOnSegment(pointOffSegment))
    }

    @Test
    fun `Should return false for disjoint segments`() {
        val firstSegment = Segment2D(
            start = Point2D(0.0, 0.0),
            end = Point2D(2.0, 0.0)
        )
        val secondSegment = Segment2D(
            start = Point2D(3.0, 1.0),
            end = Point2D(3.0, 2.0)
        )

        val hasIntersection = firstSegment.intersects(secondSegment)

        assertFalse(hasIntersection)
    }
}