package geodesy

import datum.WGS84
import model.Coordinates
import model.ECEF
import model.ENU
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

fun ECEF.toCoordinates() : Coordinates {
    val horizontalDistance = sqrt(this.x.pow(2.0) + this.y.pow(2.0))
    val auxiliaryAngle = atan2(this.z * WGS84.A, horizontalDistance * WGS84.B)

    val long = atan2(this.y, this.x)
    val lat = atan2(
        this.z + WGS84.E_PRIME2 * WGS84.B * sin(auxiliaryAngle).pow(3.0),
        horizontalDistance - WGS84.E2 * WGS84.A * cos(auxiliaryAngle).pow(3.0)
    )

    val n = lat.primeVerticalRadius()

    val alt = (horizontalDistance / cos(lat)) - n

    val latDegrees = Math.toDegrees(lat)
    val longDegrees = Math.toDegrees(long)

    val coordinates = Coordinates(latitude = latDegrees, longitude = longDegrees, altitude = alt)
    return coordinates
}

fun Coordinates.toECEF() : ECEF {
    val radianLat = Math.toRadians(this.latitude)
    val radianLong = Math.toRadians(this.longitude)

    val n = radianLat.primeVerticalRadius()

    val x = (n + this.altitude) * cos(radianLat) * cos(radianLong)
    val y = (n + this.altitude) * cos(radianLat) * sin(radianLong)
    val z = (n * (1 - WGS84.E2) + this.altitude) * sin(radianLat)

    val ecef = ECEF(x, y, z)

    return ecef
}

fun ECEF.toENU(anchor: Coordinates) : ENU {
    val anchorEcef = anchor.toECEF()

    val deltaX = this.x - anchorEcef.x
    val deltaY = this.y - anchorEcef.y
    val deltaZ = this.z - anchorEcef.z

    val latitude = Math.toRadians(anchor.latitude)
    val longitude = Math.toRadians(anchor.longitude)

    val east = -sin(longitude) * deltaX + cos(longitude) * deltaY
    val north = -sin(latitude) * cos(longitude) * deltaX - sin(latitude) * sin(longitude) * deltaY + cos(latitude) * deltaZ
    val up = cos(latitude) * cos(longitude) * deltaX + cos(latitude) * sin(longitude) * deltaY + sin(latitude) * deltaZ

    return ENU(east, north, up)
}

fun ENU.toECEF(anchor: Coordinates) : ECEF {
    val anchorEcef = anchor.toECEF()

    val latitude = Math.toRadians(anchor.latitude)
    val longitude = Math.toRadians(anchor.longitude)

    val deltaX = -sin(longitude) * this.east - sin(latitude) * cos(longitude) * this.north + cos(latitude) * cos(longitude) * this.up
    val deltaY = cos(longitude) * this.east - sin(latitude) * sin(longitude) * this.north + cos(latitude) * sin(longitude) * this.up
    val deltaZ = cos(latitude) * this.north + sin(latitude) * this.up

    val x = anchorEcef.x + deltaX
    val y = anchorEcef.y + deltaY
    val z = anchorEcef.z + deltaZ

    return ECEF(x, y, z)
}

fun Double.primeVerticalRadius() = WGS84.A / (sqrt(1 - WGS84.E2 * sin(this).pow(2.0)))