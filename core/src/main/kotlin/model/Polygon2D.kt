package model

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import spatial.contains

data class Polygon2D(
    val exteriorRing: LinearRing2D,
    val interiorRings: ImmutableList<LinearRing2D> = persistentListOf()
) {
    init {
        for (hole in interiorRings) {
            validateHoleInsideExterior(hole)
        }

        for (firstHoleIndex in interiorRings.indices) {
            for (secondHoleIndex in firstHoleIndex + 1 until interiorRings.size) {
                validateHolePair(interiorRings[firstHoleIndex], interiorRings[secondHoleIndex])
            }
        }
    }

    private fun validateHoleInsideExterior(hole: LinearRing2D) {
        require(hole.vertices.all(exteriorRing::contains)) {
            "Every hole vertex must be strictly inside the exterior ring."
        }

        require(!ringsIntersect(hole, exteriorRing)) {
            "Hole edges cannot cross or touch the exterior boundary."
        }
    }

    private fun validateHolePair(firstHole: LinearRing2D, secondHole: LinearRing2D) {
        require(!ringsIntersect(firstHole, secondHole)) {
            "Holes cannot overlap or touch."
        }

        require(
            !firstHole.contains(secondHole.vertices.first()) &&
                !secondHole.contains(firstHole.vertices.first())
        ) {
            "A hole cannot contain another hole."
        }
    }

    private fun ringsIntersect(first: LinearRing2D, second: LinearRing2D): Boolean =
        first.segments.any { firstEdge ->
            second.segments.any { secondEdge -> firstEdge.intersects(secondEdge) }
        }

    /**
     * Polygon general bounding box (exclusively defined by its outer ring)
     */
    val boundingBox: BoundingBox2D get() = exteriorRing.boundingBox
}
