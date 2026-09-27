import model.BoundingBox2D
import model.Point2D
import model.RTree2D
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RTree2DTest {

    @Test
    fun `Should find the points inside and ignore points outside`() {
        val rTree = RTree2D<String>(maxEntries = 4)

        val insidePoint1 = Point2D(2.0, 2.0)
        val insidePoint2 = Point2D(3.0, 4.0)
        val insidePoint3 = Point2D(10.0, 10.0)

        rTree.insert(insidePoint1, "P1")
        rTree.insert(insidePoint2, "P2")
        rTree.insert(insidePoint3, "P3")

        val queryBox = BoundingBox2D(minX = 1.0, minY = 1.0, maxX = 5.0, maxY = 5.0)

        val results = rTree.search(queryBox)

        assertEquals(2, results.size, "Should find exactly 2 points inside the window")
        assertTrue(results.contains("P1"))
        assertTrue(results.contains("P2"))
        assertTrue(!results.contains("P3"), "Outside point should not be in the query results")
    }

    @Test
    fun `Should return empty list when no items fall inside the query box`() {
        val rTree = RTree2D<Point2D>(maxEntries = 4)
        rTree.insert(Point2D(0.0, 0.0), Point2D(0.0, 0.0))
        rTree.insert(Point2D(1.0, 1.0), Point2D(1.0, 1.0))

        val farAwayQuery = BoundingBox2D(minX = 50.0, minY = 50.0, maxX = 60.0, maxY = 60.0)

        val results = rTree.search(farAwayQuery)

        assertTrue(results.isEmpty(), "Results should be empty when no points match")
    }

    @Test
    fun `Should maintain data integrity and split nodes when capacity is exceeded`() {
        val rTree2D = RTree2D<Int>(maxEntries = 4)
        val totalPoints = 20

        for (i in 1..totalPoints) {
            val p = Point2D(i.toDouble(), i.toDouble())
            rTree2D.insert(p, i)
        }

        val queryBox = BoundingBox2D(minX = 4.5, minY = 4.5, maxX = 10.5, maxY = 10.5)

        val results = rTree2D.search(queryBox)

        assertEquals(6, results.size, "Should retrieve all 6 points despite tree splits")
        for (expectedId in 5..10) {
            assertTrue(results.contains(expectedId), "Expected point $expectedId was not found")
        }
    }
}