package formatter

import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

fun Double.formatNumber() : String {
    val symbols = DecimalFormatSymbols(Locale.US)
    val df = DecimalFormat("#.######", symbols).apply {
        roundingMode = RoundingMode.FLOOR
    }

    return df.format(this)
}

