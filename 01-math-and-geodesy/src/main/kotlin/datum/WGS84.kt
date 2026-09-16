package datum

object WGS84 {
    const val A = 6378137.0
    const val B = 6356752.314245
    const val F = 1.0 / 298.257223563
    const val E2 = 2 * F - F * F
    const val E_PRIME2 = (A * A - B * B) / (B * B)
}