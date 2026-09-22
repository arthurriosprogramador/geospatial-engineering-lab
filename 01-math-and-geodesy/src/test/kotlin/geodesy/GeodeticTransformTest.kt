package geodesy

import datum.WGS84
import model.Coordinates
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class GeodeticTransformTest {
    private val degreeTolerance = 1e-5
    private val meterTolerance = 0.01

    @Test
    fun `should convert Geodetic to ECEF on Equator and Meridian zero`() {
        val origin = Coordinates(latitude = 0.0, longitude = 0.0, altitude = 0.0)
        val ecef = origin.toECEF()

        assertEquals(6378137.0, ecef.x, meterTolerance)
        assertEquals(0.0, ecef.y, meterTolerance)
        assertEquals(0.0, ecef.z, meterTolerance)
    }

    @Test
    fun `should convert ECEF to Geodetic on the roundtrip`() {
        val original = Coordinates(latitude = -19.747200, longitude = -47.939200, altitude = 750.0)

        val ecef = original.toECEF()
        val recovered = ecef.toCoordinates()

        assertEquals(original.latitude, recovered.latitude, degreeTolerance, "Error on Latitude")
        assertEquals(original.longitude, recovered.longitude, degreeTolerance, "Error on Longitude")
        assertEquals(original.altitude, recovered.altitude, meterTolerance, "Error on Altitude")
    }

    @Test
    fun `should return the prime vertical radius`() {
        val degreesLat = -19.747200
        val radianLat = Math.toRadians(degreesLat)
        val primeVerticalRadius = WGS84.A / (sqrt(1 - WGS84.E2 * sin(radianLat).pow(2.0)))

        val expectedN = 6380575.51

        assertEquals(expectedN, primeVerticalRadius, meterTolerance)
    }
}