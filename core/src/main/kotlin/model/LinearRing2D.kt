package model

data class LinearRing2D(
    val vertices: List<Point2D>,
) {
    init {
        require(vertices.size >= 3){
            "A geometric ring needs at least three points."
        }
    }

    /**
     * Generate the segment list (edges) that forms the closed contour.
     *
     * It bounds each vertex i to i+1, and the last one to the first one.
     */
    val segments: List<Segment2D> by lazy {
        vertices.indices.map { i ->
            val nextIndex = (i + 1) % vertices.size
            Segment2D(vertices[i], vertices[nextIndex])
        }
    }

    /**
     * Bounding box surrounding this ring
     */
    val boundingBox: BoundingBox2D by lazy {
        var minX = Double.POSITIVE_INFINITY
        var minY = Double.POSITIVE_INFINITY
        var maxX = Double.NEGATIVE_INFINITY
        var maxY = Double.NEGATIVE_INFINITY

        for (v in vertices) {
            if (v.x < minX) minX = v.x
            if (v.y < minY) minY = v.y
            if (v.x > maxX) maxX = v.x
            if (v.y > maxY) maxY = v.y
        }

        BoundingBox2D(minX, minY, maxX, maxY)
    }
}
