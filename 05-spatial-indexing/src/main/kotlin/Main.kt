import model.BoundingBox2D
import model.Point2D
import model.RTree2D

private const val pointsPrompt = "Please type the point coordinates separated by space and use comma to " +
        "separate points (E.g.: 0 0, 3 4):"

fun main() {
    startProgram()
}

private fun startProgram() {
    printTitle("Hi, welcome to the the spatial indexing system.")

    createMainMenu()
}

private fun createMainMenu()  {
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

    if (option == 2) createMainMenu()
    else {
        var pointQuantity = 0
        var isPointQuantityConfirmed = false
        var pointList: List<Point2D> = emptyList()

        while (!isPointQuantityConfirmed) {
            pointQuantity = readPointQuantity()
            if (pointQuantity <= 0) {
                println("Quantity must be greater than 0.")
                continue
            }
            isPointQuantityConfirmed = readBoolean("Great! You will inform $pointQuantity point(s). Do you confirm? (y/n)")
        }

        pointList = readPointList(pointsPrompt, pointQuantity > 1, pointQuantity)

        val rTree = RTree2D<Point2D>()

        pointList.forEach {
            rTree.insert(it, it)
        }

        println("\nNow you must define the bounding box to query the R-Tree:")
        val minX = readDouble("Min X: ")
        val maxX = readDouble("Max X: ")
        val minY = readDouble("Min Y: ")
        val maxY = readDouble("Max Y: ")

        val queryBox = BoundingBox2D(minX, minY, maxX, maxY)

        val rTreeResult = rTree.search(queryBox)

        val linearResults = pointList.filter { queryBox.contains(it) }

        println()
    }
}

private fun readPointQuantity() : Int {
    return readInt("Type how many points do you want: ")
}