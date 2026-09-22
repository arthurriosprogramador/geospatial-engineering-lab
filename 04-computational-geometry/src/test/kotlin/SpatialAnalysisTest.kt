import model.LinearRing2D
import model.Point2D
import model.Polygon2D
import org.junit.jupiter.api.Assertions.assertFalse
import spatial.contains
import kotlin.test.Test
import kotlin.test.assertTrue

class SpatialAnalysisTest {
    private val exterior = LinearRing2D(
        listOf(
            Point2D(0.0, 0.0),
            Point2D(10.0, 0.0),
            Point2D(10.0, 10.0),
            Point2D(0.0, 10.0)
        )
    )

    private val hole = LinearRing2D(
        listOf(
            Point2D(3.0, 3.0),
            Point2D(7.0, 3.0),
            Point2D(7.0, 7.0),
            Point2D(3.0, 7.0)
        )
    )

    private val polygonWithHole = Polygon2D(
        exteriorRing = exterior,
        interiorRings = listOf(hole)
    )

    @Test
    fun `should detect point clearly inside linear ring`() {
        val insidePoint = Point2D(5.0, 5.0)
        assertTrue(exterior.contains(insidePoint))
    }

    @Test
    fun `should detect point clearly outside linear ring`() {
        val outsidePoint = Point2D(15.0, 5.0)
        assertFalse(exterior.contains(outsidePoint))
    }

    @Test
    fun `should reject point immediately if outside bounding box`() {
        val farPoint = Point2D(-1.0, 5.0)
        assertFalse(exterior.contains(farPoint))
    }

    @Test
    fun `should handle horizontal ray passing exactly through a vertex`() {
        val testPoint = Point2D(5.0, 10.0)
        val isInsideOrOnBoundary = exterior.contains(testPoint)

        assertFalse(isInsideOrOnBoundary)
    }

    @Test
    fun `polygon with hole should accept point in solid area`() {
        val solidAreaPoint = Point2D(1.0, 1.0)

        assertTrue(polygonWithHole.contains(solidAreaPoint))
    }

    @Test
    fun `polygon with hole should reject point located inside the hole`() {
        val holePoint = Point2D(5.0, 5.0)

        assertFalse(polygonWithHole.contains(holePoint))
    }
}