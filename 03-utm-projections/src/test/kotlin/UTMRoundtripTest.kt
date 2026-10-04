import datum.WGS84
import geodesy.toCoordinates
import geodesy.toUTM
import model.Coordinates
import model.ECEF
import model.Hemisphere
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class UTMRoundtripTest {
    private val tolerance = 1e-5
    private val meterTolerance = 0.001

    @Test
    fun `Should perform roundtrip conversion between Geodetic and UTM preserving coordinates`() {
        val coordinates = Coordinates(latitude = -19.7472, longitude = -47.9392, altitude = 0.0)

        val utm = coordinates.toUTM()
        val reconstructed = utm.toCoordinates()

        assertEquals(
            coordinates.latitude,
            reconstructed.latitude,
            tolerance,
            "The reconstructed latitude should be equal to the original Latitude"
        )

        assertEquals(
            coordinates.longitude,
            reconstructed.longitude,
            tolerance,
            "The reconstructed longitude should be equal to the original longitude"
        )

        assertEquals(
            coordinates.altitude,
            reconstructed.altitude,
            tolerance,
            "The reconstructed altitude should be equal to the original altitude"
        )
    }

    @Test
    fun `Should transform coordinates to UTM 31N`() {
        val coordinates = Coordinates(
            latitude = 0.0,
            longitude = 3.0,
            altitude = 0.0,
        )

        val utm = coordinates.toUTM()

        assertEquals(500_000.0, utm.easting, meterTolerance)
        assertEquals(0.0, utm.northing, meterTolerance)
        assertEquals(31, utm.zone)
        assertEquals(Hemisphere.NORTH, utm.hemisphere)
    }

    @Test
    fun `should convert north pole ECEF to geodetic coordinates`() {
        val ecef = ECEF(
            x = 0.0,
            y = 0.0,
            z = WGS84.B
        )

        val coordinates = ecef.toCoordinates()

        println("ECEF: x=${ecef.x}, y=${ecef.y}, z=${ecef.z}")
        println("Latitude: radians=${Math.toRadians(coordinates.latitude)}, degrees=${coordinates.latitude}")
        println("Altitude: ${coordinates.altitude}")

        assertEquals(
            expected = 90.0,
            actual = coordinates.latitude
        )
        assertEquals(
            expected = 0.0,
            actual = coordinates.altitude
        )
    }

    @Test
    fun `should convert north pole at 100m altitude ECEF to geodetic coordinates`() {
        val ecef = ECEF(
            x = 0.0,
            y = 0.0,
            z = WGS84.B + 100.0
        )

        val coordinates = ecef.toCoordinates()

        println("ECEF: x=${ecef.x}, y=${ecef.y}, z=${ecef.z}")
        println("Latitude: radians=${Math.toRadians(coordinates.latitude)}, degrees=${coordinates.latitude}")
        println("Altitude: ${coordinates.altitude}")

        assertEquals(
            expected = 90.0,
            actual = coordinates.latitude
        )
        assertEquals(
            expected = 100.0,
            actual = coordinates.altitude
        )
    }

    @Test
    fun `should convert south pole at 0m altitude ECEF to geodetic coordinates`() {
        val ecef = ECEF(
            x = 0.0,
            y = 0.0,
            z = -WGS84.B
        )

        val coordinates = ecef.toCoordinates()

        println("ECEF: x=${ecef.x}, y=${ecef.y}, z=${ecef.z}")
        println("Latitude: radians=${Math.toRadians(coordinates.latitude)}, degrees=${coordinates.latitude}")
        println("Altitude: ${coordinates.altitude}")

        assertEquals(
            expected = -90.0,
            actual = coordinates.latitude
        )
        assertEquals(
            expected = 0.0,
            actual = coordinates.altitude
        )
    }
}