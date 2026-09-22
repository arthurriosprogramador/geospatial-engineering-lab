import geodesy.toCoordinates
import geodesy.toUTM
import model.Coordinates
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class UTMRoundtripTest {
    private val tolerance = 1e-5

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
}