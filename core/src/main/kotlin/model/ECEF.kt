package model

data class ECEF(
    val x: Double,
    val y: Double,
    val z: Double
) {
    init {
        require(x.isFinite()) { "x must be finite." }
        require(y.isFinite()) { "y must be finite." }
        require(z.isFinite()) { "z must be finite." }
    }
}