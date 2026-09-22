package formatter

import kotlin.math.floor
import kotlin.math.pow

fun Double.formatNumber(decimalPlaces: Int = 6) : String {
    val precision = 10.0.pow(decimalPlaces)
    val truncated = floor(this * precision) / precision

    return truncated.toString().removeSuffix(".0")
}

