package model

data class Point2D(
    val x : Double,
    val y: Double
) {
    /**
     * Subtract two points results in a vector that points to [other] until [this].
     *
     * E.g.: B - A = vector from A to B.
     */
    operator fun minus(point: Point2D) : Vector2D = Vector2D(x - point.x, y - point.y)

    /**
     * Sum a point with a vector shifts a point in the space.
     */
    operator fun plus (vector: Vector2D) : Point2D = Point2D(x + vector.dx, y + vector.dy)

    /**
     * Simple Euclidean distance until another point
     */
    fun distanceTo(point : Point2D) : Double = (this - point).magnitude()
}
