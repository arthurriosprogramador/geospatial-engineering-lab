import formatter.formatNumber
import geodesy.toCoordinates
import geodesy.toUTM
import model.Coordinates
import model.Hemisphere
import model.UTMCoordinates
import utils.readDouble
import utils.readInt
import utils.readString

fun main() {
    startProgram()
}

private fun startProgram() {
    val options = listOf(
        "Coordinates -> UTM",
        "UTM -> Coordinates",
        "Exit"
    )

    println("Hi, welcome to the UTM/Geodetic converter!")
    println("--------------------------------------------------")

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
        options.forEachIndexed { index, option ->
            println("${index + 1} - $option")
        }

        print("Type the desired option (1-${options.size}): ")
        val input = readlnOrNull()?.trim()
        val choice = input?.toIntOrNull()

        if (choice != null && choice in 1..options.size) {
            return choice
        } else {
            println("Invalid option. Try again!\n")
        }
    }
}

private fun chooseCalculusOption(option: Int) {
    when (option) {
        1 -> calculateCoordinatesToUTM()
        2 -> calculateUTMToCoordinate()
    }
}

private fun calculateCoordinatesToUTM() {
    println("=======================================\n=======================================")
    println("Coordinates:")
    println("=======================================\n=======================================")
    val latitude = readDouble("Enter anchor latitude (e.g.: -47.356): ")
    val longitude = readDouble("Enter anchor longitude (e.g.: -47.356): ")
    val coordinates = Coordinates(latitude, longitude, 0.0)
    println()

    val utm = coordinates.toUTM()

    println("Easting: ${utm.easting.formatNumber()}m, Northing: ${utm.northing.formatNumber()}m, Zone: ${utm.getFormattedZone()}\n")
}

private fun calculateUTMToCoordinate() {
    println("=======================================\n=======================================")
    println("UTM coordinates:")
    println("=======================================\n=======================================")
    val easting = readDouble("Type Easting (e.g.: 500453.32): ")
    val northing = readDouble("Type Northing (e.g.: 10000000.68): ")
    var zone = readInt("Type Zone (e.g.: 24): ")

    if (zone !in 1..60) {
        println("Invalid Zone! The zone should be between 1 and 60")
        zone = readInt("Type Zone (e.g.: 24): ")
    }

    var hemisphereInput = ""
    while (true) {
        hemisphereInput = readString("\nType N to North or S to South (N/S): ")

        if (hemisphereInput.lowercase() == "n" || hemisphereInput.lowercase() == "s") break
        else hemisphereInput = readString("Invalid input. Please type N to North or S to South (N/S): ")
    }
    val hemisphere = Hemisphere.isNorthOrSouth(hemisphereInput)
    val utm = UTMCoordinates(easting, northing, zone, hemisphere)
    println()

    val coordinates = utm.toCoordinates()

    println("Latitude: ${coordinates.latitude.formatNumber()}, Longitude: ${coordinates.longitude.formatNumber()}\n")
}