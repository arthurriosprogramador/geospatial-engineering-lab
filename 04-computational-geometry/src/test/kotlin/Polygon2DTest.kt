import kotlinx.collections.immutable.persistentListOf
import model.LinearRing2D
import model.Point2D
import model.Polygon2D
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class Polygon2DTest {

    private fun square(
        minX: Double,
        minY: Double,
        maxX: Double,
        maxY: Double
    ) = LinearRing2D(
        persistentListOf(
            Point2D(minX, minY),
            Point2D(maxX, minY),
            Point2D(maxX, maxY),
            Point2D(minX, maxY)
        )
    )

    private val exterior = square(0.0, 0.0, 10.0, 10.0)

    @Test
    fun `Should reject a hole outside the exterior`() {
        val hole = square(12.0, 12.0, 14.0, 14.0)

        assertFailsWith<IllegalArgumentException> {
            Polygon2D(exterior, persistentListOf(hole))
        }
    }

    @Test
    fun `Should reject a hole touching the exterior`() {
        val hole = square(0.0, 2.0, 3.0, 4.0)

        assertFailsWith<IllegalArgumentException> {
            Polygon2D(exterior, persistentListOf(hole))
        }
    }

    @Test
    fun `Should reject overlapping holes`() {
        val firstHole = square(2.0, 2.0, 5.0, 5.0)
        val secondHole = square(4.0, 4.0, 7.0, 7.0)

        assertFailsWith<IllegalArgumentException> {
            Polygon2D(
                exterior,
                persistentListOf(firstHole, secondHole)
            )
        }
    }

    @Test
    fun `Should reject touching holes`() {
        val firstHole = square(2.0, 2.0, 4.0, 4.0)
        val secondHole = square(4.0, 2.0, 6.0, 4.0)

        assertFailsWith<IllegalArgumentException> {
            Polygon2D(
                exterior,
                persistentListOf(firstHole, secondHole)
            )
        }
    }

    @Test
    fun `Should reject nested holes`() {
        val largerHole = square(2.0, 2.0, 8.0, 8.0)
        val smallerHole = square(3.0, 3.0, 4.0, 4.0)

        assertFailsWith<IllegalArgumentException> {
            Polygon2D(
                exterior,
                persistentListOf(largerHole, smallerHole)
            )
        }
    }

    @Test
    fun `Should accept two separate holes inside the exterior`() {
        val holes = persistentListOf(
            square(1.0, 1.0, 3.0, 3.0),
            square(6.0, 6.0, 8.0, 8.0)
        )

        val polygon = Polygon2D(exterior, holes)

        assertEquals(holes, polygon.interiorRings)
    }

    @Test
    fun `Should reject a hole edge crossing outside a concave exterior`() {
        val concaveExterior = LinearRing2D(
            persistentListOf(
                Point2D(0.0, 0.0),
                Point2D(10.0, 0.0),
                Point2D(10.0, 10.0),
                Point2D(6.0, 10.0),
                Point2D(6.0, 4.0),
                Point2D(4.0, 4.0),
                Point2D(4.0, 10.0),
                Point2D(0.0, 10.0)
            )
        )
        val hole = square(2.0, 6.0, 8.0, 8.0)

        assertFailsWith<IllegalArgumentException> {
            Polygon2D(concaveExterior, persistentListOf(hole))
        }
    }
}