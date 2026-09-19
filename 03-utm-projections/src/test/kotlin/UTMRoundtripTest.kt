import geodesy.toCoordinates
import geodesy.toUTM
import model.Coordinates
import org.junit.jupiter.api.Test

class UTMRoundtripTest {

    @Test
    fun `Should convert Geodetic Coordinates to UTM Coordinates`() {
        val coords = Coordinates(latitude = -19.7472, longitude = -47.9392, altitude = 0.0)
        val utm = coords.toUTM()

        println("Zone: ${utm.getFormattedZone()}")
        println("Easting:  ${utm.easting}")
        println("Northing: ${utm.northing}")
    }

    @Test
    fun `Should convert UTM Coordinates to Geodetic Coordinates`() {
        val original = Coordinates(latitude = -19.7472, longitude = -47.9392, altitude = 0.0)
        val utm = original.toUTM()
        val reconstructed = utm.toCoordinates()

        println("Original:      ${original.latitude}, ${original.longitude}")
        println("Reconstructed: ${reconstructed.latitude}, ${reconstructed.longitude}")
    }
}