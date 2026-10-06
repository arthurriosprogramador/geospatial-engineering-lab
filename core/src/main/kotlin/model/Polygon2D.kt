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
            require(hole.vertices.all { vertex ->
                exteriorRing.contains(vertex)
            }) {
                "Every hole vertex must be strictly inside the exterior ring."
            }

            require(hole.segments.none { holeEdge ->
                exteriorRing.segments.any { exteriorEdge ->
                    holeEdge.intersects(exteriorEdge)
                }
            }) {
                "Hole edges cannot cross or touch the exterior boundary."
            }
        }

        for (firstHoleIndex in interiorRings.indices) {
            for (secondHoleIndex in firstHoleIndex + 1 until interiorRings.size) {
                val firstHole = interiorRings[firstHoleIndex]
                val secondHole = interiorRings[secondHoleIndex]

                require(firstHole.segments.none { firstEdge ->
                    secondHole.segments.any { secondEdge ->
                        firstEdge.intersects(secondEdge)
                    }
                }) {
                    "Holes cannot overlap or touch."
                }

                require(
                    !firstHole.contains(secondHole.vertices.first()) &&
                            !secondHole.contains(firstHole.vertices.first())
                ) {
                    "A hole cannot contain another hole."
                }
            }
        }
    }
    /**
     * Polygon general bounding box (exclusively defined by its outer ring)
     */
    val boundingBox: BoundingBox2D get() = exteriorRing.boundingBox
}
