import area.area
import area.isCounterClockwise
import area.signedArea
import model.LinearRing2D
import model.Point2D
import model.Polygon2D
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AreaAnalysisTest {
    private val tolerance = 1e-6

    @Test
    fun `should calculate area of a simple square correctly`() {
        val squareRing = LinearRing2D(
            listOf(
                Point2D(0.0, 0.0),
                Point2D(10.0, 0.0),
                Point2D(10.0, 10.0),
                Point2D(0.0, 10.0)
            )
        )

        assertEquals(100.0, squareRing.area(), tolerance)
        assertTrue(squareRing.isCounterClockwise())
        assertTrue(squareRing.signedArea() > 0.0)
    }

    @Test
    fun `should detect clockwise winding order as negative signed area`() {
        val cwSquare = LinearRing2D(
            listOf(
                Point2D(0.0, 0.0),
                Point2D(0.0, 10.0),
                Point2D(10.0, 10.0),
                Point2D(10.0, 0.0)
            )
        )

        assertEquals(100.0, cwSquare.area(), tolerance)
        assertFalse(cwSquare.isCounterClockwise())
        assertTrue(cwSquare.signedArea() < 0.0)
    }

    @Test
    fun `should subtract holes area from polygon net area`() {
        val exterior = LinearRing2D(
            listOf(
                Point2D(0.0, 0.0),
                Point2D(20.0, 0.0),
                Point2D(20.0, 20.0),
                Point2D(0.0, 20.0)
            )
        )

        val hole = LinearRing2D(
            listOf(
                Point2D(5.0, 5.0),
                Point2D(9.0, 5.0),
                Point2D(9.0, 9.0),
                Point2D(5.0, 9.0)
            )
        )

        val polygonWithHole = Polygon2D(
            exteriorRing = exterior,
            interiorRings = listOf(hole)
        )

        assertEquals(384.0, polygonWithHole.area(), tolerance)
    }
}