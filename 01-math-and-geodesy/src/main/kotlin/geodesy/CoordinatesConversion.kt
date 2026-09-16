package geodesy

import datum.WGS84
import model.Coordinates
import model.ECEF
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

fun Double.primeVerticalRadius() = WGS84.A / (sqrt(1 - WGS84.E2 * sin(this).pow(2.0)))