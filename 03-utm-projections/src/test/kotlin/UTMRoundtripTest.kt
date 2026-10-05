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

    /**
     * Checks WGS84 UTM coordinates and zones for locations in both hemispheres.
     * Expected values have three decimal places; allowed error is 0.5 metres.
     */
    @Test
    fun `Should convert Coordinates to UTM correctly`() {
        data class ReferenceCase(
            val name: String,
            val latitude: Double,
            val longitude: Double,
            val easting: Double,
            val northing: Double,
            val zone: String
        )

        val locations = listOf(
            ReferenceCase("São Paulo", -23.5505, -46.6333, 333_287.915, 7_394_588.319, "23S"),
            ReferenceCase("New York", 40.7128, -74.0060, 583_959.372, 4_507_350.998, "18N"),
            ReferenceCase("Sydney", -33.8688, 151.2093, 334_368.634, 6_250_948.345, "56S"),
            ReferenceCase("Equator", 0.0, 0.0, 166_021.443, 0.0, "31N")
        )

        for (location in locations) {
            val utm = Coordinates(location.latitude, location.longitude, altitude = 0.0).toUTM()

            assertEquals(location.easting, utm.easting, 0.5, "${location.name} easting")
            assertEquals(location.northing, utm.northing, 0.5, "${location.name} northing")
            assertEquals(location.zone, utm.getFormattedZone(), "${location.name} UTM zone")
        }
    }
}
