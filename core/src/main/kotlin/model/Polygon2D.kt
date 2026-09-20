package model

data class Polygon2D(
    val exteriorRing: LinearRing2D,
    val interiorRings: List<LinearRing2D> = emptyList()
) {
    /**
     * Polygon general bounding box (exclusively defined by its outer ring)
     */
    val boundingBox: BoundingBox2D get() = exteriorRing.boundingBox
}
