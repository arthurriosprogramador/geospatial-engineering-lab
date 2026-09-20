package model

data class BoundingBox2D(
    val minX: Double,
    val minY: Double,
    val maxX: Double,
    val maxY: Double
) {
    /**
     * Verify if a point is inside the bounding box.
     */
    fun contains(point: Point2D) : Boolean = point.x in minX..maxX && point.y in minY..maxY

    /**
     * Verify if another bounding box intersects the bounding box
     */
    fun intersects(boundingBox: BoundingBox2D) : Boolean =
        this.minX <= boundingBox.maxX
                && this.minY <= boundingBox.maxY
                && this.maxX >= boundingBox.minX
                && this.maxY >= boundingBox.minY
}
