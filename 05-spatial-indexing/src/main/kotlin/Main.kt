import model.Point2D

fun main() {
    startProgram()
}

private fun startProgram() {
    printTitle("Hi, welcome to the the spatial indexing system.")

    val options = listOf(
        "Manual Points Input",
        "Benchmark Mode with Random Dataset",
        "Exit"
    )

    val option = createCLIMenu(options, "Main menu")

    if (option == 3) printFarewell()
    else handleOption(option)
}

private fun handleOption(option: Int) {
    when (option) {
        1 -> handleManualPoints()
    }
}

private fun handleManualPoints() {
    val manualPointsOptions = listOf(
        "Inform how many points.",
        "Previous menu"
    )

    val option = createCLIMenu(manualPointsOptions, "Manual Points Menu")

    if (option == 2) printFarewell()
    else {
        var pointQuantity = readPointQuantity()
        val isPointQuantityConfirmed = readBoolean("Great! You will inform $pointQuantity point(s). Do you confirm?")
        var pointList: List<Point2D> = if (isPointQuantityConfirmed) {
            readPointList("Please type the point coordinates separated by space and use comma to " +
                    "separate points (E.g.: 0 0, 3 4):")
        } else  listOf()
    }
}

private fun readPointQuantity() : Int {
    return readInt("Type how many points do you want: ")
}