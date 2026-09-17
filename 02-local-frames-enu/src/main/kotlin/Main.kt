import formatter.formatNumber
import geodesy.toECEF
import geodesy.toENU
import model.Coordinates
import model.ENU
import utils.readDouble

fun main() {
    val options = listOf(
        "ECEF -> ENU",
        "ENU -> ECEF",
        "Exit"
    )

    startProgram(options)
}

private fun startProgram(options: List<String>) {
    println("Hi, welcome to ENU/ECEF converter!")
    println("=======================================\n=======================================")
    println("To convert the ENU to ECEF - or vice-versa - you'll need an anchor coordinate and a target coordinate.")
    while (true) {
        val selectedOption = selectOption(options)

        if (selectedOption == 3) {
            println("Exiting application. Goodbye!")
            break
        }

        chooseCalculusOption(selectedOption)
    }
}

private fun selectOption(options: List<String>): Int {
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
        1 -> calculateECEFToENU()
        2 -> calculateENUToECEF()
    }
}

private fun calculateECEFToENU() {
    println("=======================================\n=======================================")
    println("Anchor coordinates:")
    println("=======================================\n=======================================")
    val anchorLatitude = readDouble("Enter anchor latitude (e.g.: -47.356): ")
    val anchorLongitude = readDouble("Enter anchor longitude (e.g.: -47.356): ")
    val anchorAltitude = readDouble("Enter anchor altitude (e.g.: -47.356): ")
    val anchor = Coordinates(anchorLatitude, anchorLongitude, anchorAltitude)
    println()

    println("=======================================\n=======================================")
    println("Target coordinates:")
    println("=======================================\n=======================================")
    val targetLatitude = readDouble("Enter target latitude (e.g.: -47.356): ")
    val targetLongitude = readDouble("Enter target longitude (e.g.: -47.356): ")
    val targetAltitude = readDouble("Enter target altitude (e.g.: -47.356): ")
    val target = Coordinates(targetLatitude, targetLongitude, targetAltitude)
    println()

    val targetEcef = target.toECEF()
    val enu = targetEcef.toENU(anchor)

    println("East: ${enu.east.formatNumber()}m\nNorth: ${enu.north.formatNumber()}m\nUp: ${enu.up.formatNumber()}m\n")
}

private fun calculateENUToECEF() {
    println("=======================================\n=======================================")
    println("Anchor ECEF:")
    println("=======================================\n=======================================")
    val anchorLatitude = readDouble("Enter anchor latitude (e.g.: -47.356): ")
    val anchorLongitude = readDouble("Enter anchor longitude (e.g.: -47.356): ")
    val anchorAltitude = readDouble("Enter anchor altitude (e.g.: -47.356): ")
    val anchor = Coordinates(anchorLatitude, anchorLongitude, anchorAltitude)
    println()

    println("=======================================\n=======================================")
    println("Target ENU:")
    println("=======================================\n=======================================")
    val targetEast = readDouble("Enter target East (e.g.: 1520,365): ")
    val targetNorth = readDouble("Enter target North (e.g.: 1520,365): ")
    val targetUp = readDouble("Enter target altitude (e.g.: 1520,365): ")
    val target = ENU(targetEast, targetNorth, targetUp)
    println()

    val ecef = target.toECEF(anchor)

    println("X: ${ecef.x.formatNumber()}m\nY: ${ecef.y.formatNumber()}m\nZ: ${ecef.z.formatNumber()}m\n")
}