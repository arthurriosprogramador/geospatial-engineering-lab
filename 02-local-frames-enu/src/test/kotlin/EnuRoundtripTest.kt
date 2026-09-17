import geodesy.toECEF
import geodesy.toENU
import model.Coordinates
import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test

class EnuRoundtripTest {
    private val tolerance = 1e-4

    @Test
    fun `should result in origin when target is the anchor itself`() {
        val anchor = Coordinates(latitude = -23.5505, longitude = -46.6333, altitude = 760.0)
        val targetEcef = anchor.toECEF()

        val enu = targetEcef.toENU(anchor)

        assertEquals(0.0, enu.east, tolerance)
        assertEquals(0.0, enu.north, tolerance)
        assertEquals(0.0, enu.up, tolerance)
    }

    @Test
    fun `should perform roundtrip between ECEF and ENU preserving coordinates`() {
        val anchor = Coordinates(latitude = -19.7472, longitude = -47.9392, altitude = 800.0)

        val originalTarget = Coordinates(latitude = -19.7500, longitude = -47.9350, altitude = 850.0)
        val originalEcef = originalTarget.toECEF()

        val enu = originalEcef.toENU(anchor)
        val reconstructedEcef = enu.toECEF(anchor)

        assertEquals(originalEcef.x, reconstructedEcef.x, tolerance)
        assertEquals(originalEcef.y, reconstructedEcef.y, tolerance)
        assertEquals(originalEcef.z, reconstructedEcef.z, tolerance)
    }
}