import geodesy.toCoordinates
import geodesy.toUTM
import model.Coordinates
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

    /**
     * Checks Norway and Svalbard UTM zone boundaries.
     * Expected zones follow [GeographicLib's rules](https://geographiclib.sourceforge.io/2009-03/UTMUPS_8cpp_source.html).
     */
    @Test
    fun `Should get the correct UTM zone for each coordinate`() {
        data class ZoneCase(
            val latitude: Double,
            val longitude: Double,
            val utmZone: Int
        )

        val location = listOf(
            ZoneCase(55.999999, 4.0, 31),
            ZoneCase(56.0,      4.0, 32),
            ZoneCase(60.0, 4.0, 32),
            ZoneCase(63.999999, 4.0, 32),
            ZoneCase(64.0,      4.0, 31),
            ZoneCase(71.999999, 10.0, 32),
            ZoneCase(72.0,      10.0, 33),
            ZoneCase(75.0, 8.0, 31),
            ZoneCase(75.0, 9.0, 33),
            ZoneCase(75.0, 21.0, 35),
            ZoneCase(75.0, 33.0, 37),
            ZoneCase(75.0, 42.0, 38)
        )

        for ((lat, long, utmZone) in location) {
            val utm = Coordinates(lat, long, 0.0).toUTM()
            assertEquals( utmZone, utm.zone)
        }
    }

    /**
     * Checks UTM coordinates against values generated with pyproj 3.7.2.
     * Input: WGS84 (EPSG:4326), longitude then latitude; output: each listed UTM zone.
     * Easting and northing are rounded to 0.001 m; tolerance is 0.5 m.
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
