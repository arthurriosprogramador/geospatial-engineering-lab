import model.BoundingBox2D
import model.Point2D
import model.RTree2D
import model.RTreeEntry
import org.junit.jupiter.api.Test
import kotlin.random.Random
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

    @Test
    fun `should match linear scan with duplicates and capacity two`() {
        val random = Random(42)

        val points = List(100) {
            Point2D(
                random.nextDouble(-100.0, 100.0),
                random.nextDouble(-100.0, 100.0)
            )
        } + List(3) { Point2D(0.0, 0.0) }

        val rTree = RTree2D<Point2D>(maxEntries = 2)
        points.forEach {
            rTree.insert(it, it)
        }

        val queryBox = BoundingBox2D(
            minX = -1.0,
            minY = -1.0,
            maxX = 1.0,
            maxY = 1.0
        )

        val expected = points.filter { queryBox.contains(it) }
        val actual = rTree.search(queryBox)

        assertEquals(
            expected.groupingBy { it }.eachCount(),
            actual.groupingBy { it }.eachCount()
        )

        repeat(10) {
            val x1 = random.nextDouble(-100.0, 100.0)
            val x2 = random.nextDouble(-100.0, 100.0)

            val y1 = random.nextDouble(-100.0, 100.0)
            val y2 = random.nextDouble(-100.0, 100.0)

            val boundingBox = BoundingBox2D(
                minX = minOf(x1, x2),
                maxX = maxOf(x1, x2),
                minY = minOf(y1, y2),
                maxY = maxOf(y1, y2)
            )

            val expected = points.filter { boundingBox.contains(it) }
            val actual = rTree.search(boundingBox)

            assertEquals(
                expected.groupingBy { it }.eachCount(),
                actual.groupingBy { it }.eachCount()
            )
        }
    }

    @Test
    fun `should include points on query edges and corners`() {
        val queryBox = BoundingBox2D(
            minX = 0.0,
            minY = 0.0,
            maxX = 10.0,
            maxY = 10.0
        )

        val rTree = RTree2D<Point2D>(maxEntries = 2)

        val pointList = listOf(
            Point2D(0.0, 5.0),
            Point2D(10.0, 5.0),
            Point2D(5.0, 0.0),
            Point2D(5.0, 10.0),
            Point2D(0.0, 0.0),
            Point2D(10.0, 10.0),
            Point2D(11.0, 5.0)
        )
        pointList.forEach {
            rTree.insert(it, it)
        }

        val expected = listOf(
            Point2D(0.0, 5.0),
            Point2D(10.0, 5.0),
            Point2D(5.0, 0.0),
            Point2D(5.0, 10.0),
            Point2D(0.0, 0.0),
            Point2D(10.0, 10.0)
        )
        val actual = rTree.search(queryBox)

        assertEquals(
            expected.groupingBy { it }.eachCount(),
            actual.groupingBy { it }.eachCount()
        )
    }

    @Test
    fun `should return no matches from an empty tree`() {
        val rTree = RTree2D<Point2D>(maxEntries = 2)
        val queryBox = BoundingBox2D(
            minX = 0.0,
            minY = 0.0,
            maxX = 10.0,
            maxY = 10.0
        )

        val actual = rTree.search(queryBox)

        assertTrue(actual.isEmpty())
    }

    @Test
    fun `should find rectangular entries intersecting the query`() {
        val queryBox = BoundingBox2D(
            minX = 0.0, minY = 0.0,
            maxX = 10.0, maxY = 10.0
        )

        val rTree = RTree2D<String>(maxEntries = 2)

        val entries = listOf(
            RTreeEntry(
                boundingBox = BoundingBox2D(
                    minX = 2.0, minY = 2.0,
                    maxX = 4.0, maxY = 4.0
                ),
                value = "inside"
            ),
            RTreeEntry(
                boundingBox = BoundingBox2D(
                    minX = 8.0, minY = 8.0,
                    maxX = 12.0, maxY = 12.0
                ),
                value = "overlapping"
            ),
            RTreeEntry(
                boundingBox = BoundingBox2D(
                    minX = -1.0, minY = -1.0,
                    maxX = 11.0, maxY = 11.0
                ),
                value = "surrounding"
            ),
            RTreeEntry(
                boundingBox = BoundingBox2D(
                    minX = 10.0, minY = 2.0,
                    maxX = 12.0, maxY = 4.0
                ),
                value = "touching"
            ),
            RTreeEntry(
                boundingBox = BoundingBox2D(
                    minX = 11.0, minY = 11.0,
                    maxX = 12.0, maxY = 12.0
                ),
                value = "outside"
            )
        )

        entries.forEach { rTree.insert(it) }

        val actual = rTree.search(queryBox)
        val expected = listOf(
            "inside", "overlapping", "surrounding", "touching"
        )

        assertEquals(
            expected.groupingBy { it }.eachCount(),
            actual.groupingBy { it }.eachCount()
        )
    }
}