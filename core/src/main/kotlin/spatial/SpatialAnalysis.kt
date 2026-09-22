package spatial

import model.Point2D
import model.Segment2D
import model.Polygon2D
import model.LinearRing2D

/**
 * Checks if a horizontal ray extending to +infinity from [point] intersects this segment.
 *
 * Handles edge conditions:
 *
 * - Rays passing exactly through a vertex (half-open vertical interval `[minY, maxY)`).
 *
 * - Horizontal edges are ignored since they don't produce valid crossings.
 */
fun Segment2D.intersectsHorizontalRay(point: Point2D) : Boolean {
    val (p1, p2) = start to end

    val inVerticalRange = (p1.y <= point.y && point.y < p2.y) || (p2.y <= point.y && point.y < p1.y)
    if (!inVerticalRange) return false

    val intersectX = p1.x + (point.y - p1.y) * (p2.x - p1.x) / (p2.y - p1.y)

    return  intersectX > point.x
}

/**
 * Checks if a [point] is inside this [LinearRing2D] using the Ray casting algorithm.
 *
 * Uses bounding box as an initial fast filter.
 *
 * @return true if a point is inside the ring, and false otherwise.
 */
fun LinearRing2D.contains(point: Point2D) : Boolean {
    if (!boundingBox.contains(point)) return false

    var intersections = 0
    for (segment in segments) {
        if (segment.intersectsHorizontalRay(point)) {
            intersections++
        }
    }

    //It's a parity, Odd = Inside and Even = Outside
    return (intersections % 2) != 0
}

/**
 * Checks if a [point] is inside this [Polygon2D], taking interior holes into account.
 *
 * A point is considered inside if:
 *
 * 1. It is inside the exterior ring.
 *
 * 2. It is **NOT** inside any of the interior rings (holes).
 */
fun Polygon2D.contains(point: Point2D) : Boolean {
    return exteriorRing.contains(point) && interiorRings.none { hole -> hole.contains(point) }
}