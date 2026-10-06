package model

import kotlinx.collections.immutable.ImmutableList

data class LinearRing2D(
    val vertices: ImmutableList<Point2D>,
) {
    init {
        require(vertices.size >= 3){
            "A geometric ring needs at least three points."
        }

        val firstVertex = vertices.first()
        val secondVertex = vertices.firstOrNull { it != firstVertex }

        require(secondVertex != null) {
            "The vertices cannot all be the same point."
        }

        val referenceSegment = Segment2D(firstVertex, secondVertex)

        require(vertices.any {
            referenceSegment.orientationOf(it) != 0.0
        }) {
            "The vertices cannot all lie on the same line."
        }

        val edges = vertices.indices.map { vertexIndex ->
            val nextVertexIndex = (vertexIndex + 1) % vertices.size
            Segment2D(vertices[vertexIndex], vertices[nextVertexIndex])
        }

        for (firstEdgeIndex in edges.indices) {
            for (secondEdgeIndex in firstEdgeIndex + 1 until edges.size) {
                val areNeighbours =
                    secondEdgeIndex == firstEdgeIndex + 1 ||
                            (firstEdgeIndex == 0 && secondEdgeIndex == edges.lastIndex)

                if (areNeighbours) continue

                val firstEdge = edges[firstEdgeIndex]
                val secondEdge = edges[secondEdgeIndex]

                require(!firstEdge.intersects(secondEdge)) {
                    "Non-neighbouring edges cannot intersect."
                }
            }
        }

        require(edges.none { edge -> edge.start == edge.end }) {
            "Consecutive vertices cannot be identical."
        }

        for (vertexIndex in vertices.indices) {
            val previousIndex =
                (vertexIndex + vertices.size - 1) % vertices.size
            val nextIndex = (vertexIndex + 1) % vertices.size

            val previousVertex = vertices[previousIndex]
            val currentVertex = vertices[vertexIndex]
            val nextVertex = vertices[nextIndex]

            val incomingEdge = Segment2D(previousVertex, currentVertex)
            val outgoingEdge = Segment2D(currentVertex, nextVertex)

            val overlaps =
                incomingEdge.isPointOnSegment(nextVertex) ||
                        outgoingEdge.isPointOnSegment(previousVertex)

            require(!overlaps) {
                "Neighbouring edges cannot fold back and overlap."
            }
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

        for ((x, y) in vertices) {
            if (x < minX) minX = x
            if (y < minY) minY = y
            if (x > maxX) maxX = x
            if (y > maxY) maxY = y
        }

        BoundingBox2D(minX, minY, maxX, maxY)
    }
}
