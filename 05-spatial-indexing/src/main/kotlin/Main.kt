import model.BoundingBox2D
import model.Point2D
import model.RTree2D
import java.io.EOFException
import kotlin.random.Random

private const val pointsPrompt = "Please type the point coordinates separated by space and use comma to " +
        "separate points (E.g.: 0 0, 3 4):"

fun main() {
    try {
        startProgram()
    } catch (_: EOFException) {
        println("\nInput ended. Goodbye!")
    }
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
        2 -> handleRandomDataset()
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

        while (!isPointQuantityConfirmed) {
            pointQuantity = readPointQuantity()
            if (pointQuantity <= 0) {
                println("Quantity must be greater than 0.")
                continue
            }
            isPointQuantityConfirmed = readBoolean("Great! You will inform $pointQuantity point(s). Do you confirm? (y/n)")
        }

        val pointList: List<Point2D> = readPointList(pointsPrompt, pointQuantity > 1, pointQuantity)

        println("\nNow you must define the bounding box to query the R-Tree:")

        val maxXPrompt = "Max X: "
        val maxYPrompt = "Max Y: "

        val minX = readDouble("Min X: ")
        var maxX = readDouble(maxXPrompt)
        while (maxX < minX) {
            println("The maximum X could not be less than the minimum X.")
            maxX = readDouble(maxXPrompt)
        }
        val minY = readDouble("Min Y: ")
        var maxY = readDouble(maxYPrompt)
        while (maxY < minY) {
            println("The maximum Y could not be less than the minimum Y.")
            maxY = readDouble(maxYPrompt)
        }

        val queryBox = BoundingBox2D(minX, minY, maxX, maxY)

        getResult(queryBox, pointList)
    }
}

private fun readPointQuantity() : Int {
    return readInt("Type how many points do you want: ")
}

private fun handleRandomDataset() {
    val minCoord = -1000.0
    val maxCoord = 1000.0
    val maxPoints = 500_000
    val pointQuantity = Random.nextInt(1, maxPoints)
    val pointList: MutableList<Point2D> = mutableListOf()
    for (p in 1..pointQuantity) {
        val x = Random.nextDouble(minCoord, maxCoord)
        val y = Random.nextDouble(minCoord, maxCoord)

        val point = Point2D(x, y)
        pointList.add(point)
    }

    val xValues = mutableListOf<Double>()
    val yValues = mutableListOf<Double>()

    repeat(2) {
        xValues.add(Random.nextDouble(minCoord, maxCoord))
        yValues.add(Random.nextDouble(minCoord, maxCoord))
    }

    val minX = xValues.min()
    val maxX = xValues.max()
    val minY = yValues.min()
    val maxY = yValues.max()

    val boundingBox = BoundingBox2D(minX, minY, maxX, maxY)
    getResult(boundingBox, pointList)
}

private fun getResult(boundingBox: BoundingBox2D, pointList: List<Point2D>) {

    val rTree2D = RTree2D<Point2D>()

    pointList.forEach {
        rTree2D.insert(it, it)
    }

    val rTreeResult = rTree2D.search(boundingBox)
    val linearResults = pointList.filter { boundingBox.contains(it) }
    val missedPoints = linearResults.filter { !rTreeResult.contains(it) }

    printStyledPrompt(
        color = CLIColors.KOTLIN_PURPLE_BOLD,
        prompt = "\nR-Tree found ${rTreeResult.size} point(s): $rTreeResult",
        background = CLIColors.BG_WHITE,
    )
    printStyledPrompt(
        color = CLIColors.KOTLIN_PURPLE_BOLD,
        prompt = "Linear scan found ${linearResults.size} point(s): $linearResults",
        background = CLIColors.BG_WHITE,
    )
    printStyledPrompt(
        color = CLIColors.KOTLIN_PURPLE_BOLD,
        prompt = "There were ${missedPoints.size} missed point(s): $missedPoints",
        background = CLIColors.BG_WHITE,
    )
}