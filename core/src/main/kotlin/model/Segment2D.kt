package model

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
}
