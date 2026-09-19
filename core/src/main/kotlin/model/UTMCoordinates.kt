package model

data class UTMCoordinates(
    val easting: Double,
    val northing: Double,
    val zone: Int,
    val hemisphere: Hemisphere
) {
    fun getFormattedZone() : String{
        val hemisphereLabel = if (hemisphere == Hemisphere.NORTH) "N" else "S"
        return "$zone$hemisphereLabel"
    }
}

enum class Hemisphere {
    NORTH, SOUTH;

    companion object {
        fun isNorthOrSouth(hemisphere: String) : Hemisphere = if (hemisphere.uppercase() == "N") NORTH else SOUTH
    }
}
