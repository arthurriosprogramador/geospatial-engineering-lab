package model

import kotlin.math.abs

data class Segment2D(
    val start: Point2D,
    val end: Point2D
) {
    /**
     * Directional vector that goes from start in direction to end.
     */
    val direction: Vector2D = end - start

    /**
     * segment linear length in meters
     */
    fun length() = start.distanceTo(end)

    /**
     * Determines the relative position of a third point related to the segment.
     *
     * `> 0.0` -> The point is at the **LEFT (Counter-Clockwise curve)**
     *
     * `< 0.0` -> The point is at the **RIGHT (Clockwise curve)**
     *
     * `== 0.0` -> The point is **Collinear**
     *
     * @param point The third point that will be analyzed.
     * @return relative position with a cross product.
     */
    fun orientationOf(point: Point2D): Double {
        val toPoint = point - start
        return direction cross toPoint
    }

    /**
     * Determines if the point is on the segment.
     */
    fun isPointOnSegment(point: Point2D, tolerance: Double = 1e-6): Boolean {
        val segmentLength = length()

        if (segmentLength == 0.0) {
            return start.distanceTo(point) <= tolerance
        }

        val distanceToLine = abs(orientationOf(point)) / segmentLength

        val withinX = point.x >= minOf(start.x, end.x) - tolerance
                && point.x <= maxOf(start.x, end.x) + tolerance

        val withinY = point.y >= minOf(start.y, end.y) - tolerance
                && point.y <= maxOf(start.y, end.y) + tolerance

        return distanceToLine <= tolerance && withinX && withinY
    }
}
