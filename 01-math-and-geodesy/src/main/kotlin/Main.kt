import formatter.formatNumber
import geodesy.toCoordinates
import geodesy.toECEF
import model.Coordinates
import model.ECEF

fun main() {
    val options = listOf(
        "Geodetic (Lat, Long, ALt) -> ECEF (X, Y, Z)",
        "ECEF (X, Y, Z) -> Geodetic (Lat, Long, Alt)",
        "Exit"
    )

    startProgram(options)
}

private fun startProgram(options: List<String>) {
    println("Hi, welcome to ECEF/Geodetic converter! What do you want to convert from?")
    while (true) {
        val selectedOption = selectOption(options)

        if (selectedOption == 3) {
            println("Exiting application. Goodbye!")
            break
        }

        chooseCalculusOption(selectedOption)
    }
}

private fun selectOption(options: List<String>) : Int {
    while (true) {
        println("Select an option: ")

        options.forEachIndexed { index, option ->
            println("${index + 1}. $option")
        }

        print("Type the desired option (1-${options.size}): ")

        val input = readlnOrNull()?.trim()
        val choice = input?.toIntOrNull()

        if (choice != null && choice in 1..options.size) {
            return choice
        } else {
            println("Invalid option. Try again.\n")
        }
    }
}

private fun chooseCalculusOption(option: Int) {
    when (option) {
        1 -> calculateGeodeticToEcef()
        2 -> calculateEcefToGeodetic()
    }
}

private fun calculateGeodeticToEcef() {
    val lat = readDouble("\nType Latitude (e.g.: -47.2): ")
    val long = readDouble("\nType Longitude (e.g.: -42.3): ")
    val alt = readDouble("\nType altitude (e.g.: 105.5): ")
    println()

    val coordinates = Coordinates(lat, long, alt)
    val ecef = coordinates.toECEF()

    println("X: ${ecef.x.formatNumber()}m\nY: ${ecef.y.formatNumber()}m\nZ: ${ecef.z.formatNumber()}m")
}

private fun calculateEcefToGeodetic() {
    val x = readDouble("\nType the X axis (e.g.:1170.2): ")
    val y = readDouble("\nType the y axis (e.g.: 1170.2): ")
    val z = readDouble("\nType the z axis (e.g.: 1170.2): ")
    println()

    val ecef = ECEF(x, y, z)
    val coordinates = ecef.toCoordinates()
    println("\nLatitude: ${coordinates.latitude.formatNumber()}º" +
            "\nLongitude: ${coordinates.longitude.formatNumber()}º" +
            "\nAltitude: ${coordinates.altitude.formatNumber()}m")
}

private fun readDouble(prompt: String): Double {
    while (true) {
        print(prompt)
        val input = readlnOrNull()?.trim()?.replace(',', '.')
        val value = input?.toDoubleOrNull()

        if (value != null) {
            return value
        }

        println("Invalid value. Try again.")
    }
}