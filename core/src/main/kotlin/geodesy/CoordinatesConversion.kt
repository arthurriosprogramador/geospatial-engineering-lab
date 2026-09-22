package geodesy

import datum.WGS84
import model.Coordinates
import model.ECEF
import model.ENU
import model.Hemisphere
import model.UTMCoordinates
import model.UTMSpatialReference
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

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

fun UTMCoordinates.toCoordinates(): Coordinates {
    val x = this.easting - UTMSpatialReference.FALSE_EASTING
    val y = if (this.hemisphere == Hemisphere.SOUTH) {
        this.northing - UTMSpatialReference.FALSE_NORTHING
    } else {
        this.northing
    }

    val centralMeridianScaleFactor = UTMSpatialReference.CENTRAL_MERIDIAN_SCALE_FACTOR
    val meridianArchMeters = y / centralMeridianScaleFactor

    val earthRectifyingRadius = WGS84.A * (
            1.0 - (WGS84.E2 / 4.0) - ((3.0 * WGS84.E2.pow(2.0)) / 64.0) - ((5.0 * WGS84.E2.pow(3.0)) / 256.0)
            )
    val mu = meridianArchMeters / earthRectifyingRadius

    val sqrtE = sqrt(1.0 - WGS84.E2)
    val e1 = (1.0 - sqrtE) / (1.0 + sqrtE)

    val footprintLatitude = mu +
            ((3.0 * e1 / 2.0) - (27.0 * e1.pow(3.0) / 32.0)) * sin(2.0 * mu) +
            ((21.0 * e1.pow(2.0) / 16.0) - (55.0 * e1.pow(4.0) / 32.0)) * sin(4.0 * mu) +
            ((151.0 * e1.pow(3.0) / 96.0)) * sin(6.0 * mu) +
            ((1097.0 * e1.pow(4.0) / 512.0)) * sin(8.0 * mu)

    val sinFootprint = sin(footprintLatitude)
    val cosFootprint = cos(footprintLatitude)
    val tanFootprint = tan(footprintLatitude)

    val nu1 = WGS84.A / sqrt(1.0 - WGS84.E2 * sinFootprint.pow(2.0))
    val rho1 = (WGS84.A * (1.0 - WGS84.E2)) / (1.0 - WGS84.E2 * sinFootprint.pow(2.0)).pow(1.5)

    val t1 = tanFootprint.pow(2.0)
    val c1 = WGS84.E_PRIME2 * cosFootprint.pow(2.0)
    val d = x / (nu1 * centralMeridianScaleFactor)

    val radianLatitude = footprintLatitude - (nu1 * tanFootprint / rho1) * (
            (d.pow(2.0) / 2.0) -
                    (5.0 + 3.0 * t1 + 10.0 * c1 - 4.0 * c1.pow(2.0) - 9.0 * WGS84.E_PRIME2) * (d.pow(4.0) / 24.0) +
                    (61.0 + 90.0 * t1 + 298.0 * c1 + 45.0 * t1.pow(2.0) - 252.0 * WGS84.E_PRIME2 - 3.0 * c1.pow(2.0)) * (d.pow(6.0) / 720.0)
            )

    val radianDeltaLong = (1.0 / cosFootprint) * (
            d -
                    (1.0 + 2.0 * t1 + c1) * (d.pow(3.0) / 6.0) +
                    (5.0 - 2.0 * c1 + 28.0 * t1 - 3.0 * c1.pow(2.0) + 8.0 * WGS84.E_PRIME2 + 24.0 * t1.pow(2.0)) * (d.pow(5.0) / 120.0)
            )

    val centralMeridian = (this.zone * 6.0) - 183.0

    val latitudeDegrees = Math.toDegrees(radianLatitude)
    val longitudeDegrees = centralMeridian + Math.toDegrees(radianDeltaLong)

    return Coordinates(latitudeDegrees, longitudeDegrees, altitude = 0.0)
}

fun Coordinates.toUTM() : UTMCoordinates {
    val zone = (((this.longitude + 180.0) / 6.0).toInt() + 1).coerceIn(1 ,60)
    val centralMeridian = (zone * 6.0) - 183.0

    val radiansLat = Math.toRadians(this.latitude)
    val radiansDeltaLong = Math.toRadians(this.longitude - centralMeridian)

    val hemisphere = if (this.latitude < 0) Hemisphere.SOUTH else Hemisphere.NORTH

    val angularOffsetTerm = cos(radiansLat) * radiansDeltaLong
    val tanLatSquared = tan(radiansLat).pow(2.0)
    val ellipsoidalCurvatureCorrection = WGS84.E_PRIME2 * cos(radiansLat).pow(2.0)

    val meridianArch = WGS84.A * (
            (1 - WGS84.E2 / 4 - (3 * WGS84.E2.pow(2.0)) / 64 - (5 * WGS84.E2.pow(3.0)) / 256) * radiansLat
                    - (3 * WGS84.E2 / 8 + (3 * WGS84.E2.pow(2.0)) / 32 + (45 * WGS84.E2.pow(3.0)) / 1024) * sin(2 * radiansLat)
                    + ((15 * WGS84.E2.pow(2.0)) / 256 + (45 * WGS84.E2.pow(3.0)) / 1024) * sin(4 * radiansLat)
                    - ((35 * WGS84.E2.pow(3.0)) / 3072) * sin(6 * radiansLat)
            )
    val easting = UTMSpatialReference.FALSE_EASTING + UTMSpatialReference.CENTRAL_MERIDIAN_SCALE_FACTOR * radiansLat.primeVerticalRadius() * (
            angularOffsetTerm + (1 - tanLatSquared + ellipsoidalCurvatureCorrection) * (angularOffsetTerm.pow(3.0) / 6.0)
                    + (5 - 18 * tanLatSquared + tanLatSquared.pow(2.0) + 72 * ellipsoidalCurvatureCorrection - 58 * WGS84.E_PRIME2) * (angularOffsetTerm.pow(5.0) / 120)
            )
    var northing =UTMSpatialReference.CENTRAL_MERIDIAN_SCALE_FACTOR * (
        meridianArch + radiansLat.primeVerticalRadius() * tan(radiansLat) * (
            (angularOffsetTerm.pow(2.0) / 2.0)
            + (5 - tanLatSquared + 9 * ellipsoidalCurvatureCorrection + 4 * ellipsoidalCurvatureCorrection.pow(2.0))
            * (angularOffsetTerm.pow(4.0) / 24)
            + (61 - 58 * tanLatSquared + tanLatSquared.pow(2.0) + 600 * ellipsoidalCurvatureCorrection - 330 * WGS84.E_PRIME2)
            * (angularOffsetTerm.pow(6.0) / 720)
        )
    )

    if (hemisphere == Hemisphere.SOUTH) {
        northing += UTMSpatialReference.FALSE_NORTHING
    }

    val utm = UTMCoordinates(
        easting = easting,
        northing = northing,
        zone = zone,
        hemisphere = hemisphere
    )

    return utm
}

fun Double.primeVerticalRadius() = WGS84.A / (sqrt(1 - WGS84.E2 * sin(this).pow(2.0)))