import model.BoundingBox2D
import model.Point2D
import model.RTree2D
import java.io.EOFException
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.measureTimedValue

private const val pointsPrompt = "Please type the point coordinates separated by space and use comma to " +
        "separate points (E.g.: 0 0, 3 4):"

fun main(args: Array<String>) {
    try {
        if ("--random" in args || "--benchmark" in args) handleRandomDataset() else startProgram()
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
    val pointSeed = 42
    val querySeed = 43
    val minCoord = -1000.0
    val maxCoord = 1000.0
    val pointRandom = Random(pointSeed)
    val queryRandom = Random(querySeed)
    val pointList = List(100_000) {
        Point2D(
            pointRandom.nextDouble(minCoord, maxCoord),
            pointRandom.nextDouble(minCoord, maxCoord)
        )
    }
    val queryBoxes = List(100) { index ->
        val halfWidth = when (index % 3) {
            0 -> 10.0
            1 -> 50.0
            else -> 200.0
        }
        val centerX = queryRandom.nextDouble(minCoord + halfWidth, maxCoord - halfWidth)
        val centerY = queryRandom.nextDouble(minCoord + halfWidth, maxCoord - halfWidth)
        BoundingBox2D(
            centerX - halfWidth, centerY - halfWidth,
            centerX + halfWidth, centerY + halfWidth
        )
    }

    runBenchmark(pointList, queryBoxes, pointSeed, querySeed)
}

private fun buildTree(points: List<Point2D>): RTree2D<Point2D> = RTree2D<Point2D>().also { tree ->
    points.forEach { tree.insert(it, it) }
}

private fun median(durations: List<Duration>): Duration = durations.sorted()[durations.size / 2]

private fun runBenchmark(
    points: List<Point2D>,
    queryBoxes: List<BoundingBox2D>,
    pointSeed: Int,
    querySeed: Int
) {
    val tree = buildTree(points)

    for ((index, box) in queryBoxes.withIndex()) {
        val treeResults = tree.search(box).groupingBy { it }.eachCount()
        val scanResults = points.filter { box.contains(it) }.groupingBy { it }.eachCount()
        check(treeResults == scanResults) { "R-tree and linear scan differ for query $index" }
    }

    fun treeBatch(): Int = queryBoxes.sumOf { tree.search(it).size }
    fun scanBatch(): Int = queryBoxes.sumOf { box -> points.filter { box.contains(it) }.size }

    val expectedMatches = scanBatch()
    repeat(5) {
        check(treeBatch() == expectedMatches)
        check(scanBatch() == expectedMatches)
    }

    val treeTimes = mutableListOf<Duration>()
    val scanTimes = mutableListOf<Duration>()
    repeat(15) { iteration ->
        fun measureTree() {
            val sample = measureTimedValue { treeBatch() }
            check(sample.value == expectedMatches)
            treeTimes.add(sample.duration)
        }
        fun measureScan() {
            val sample = measureTimedValue { scanBatch() }
            check(sample.value == expectedMatches)
            scanTimes.add(sample.duration)
        }

        if (iteration % 2 == 0) {
            measureTree()
            measureScan()
        } else {
            measureScan()
            measureTree()
        }
    }

    repeat(2) { buildTree(points) }
    val buildTimes = List(7) {
        val sample = measureTimedValue { buildTree(points) }
        check(sample.value.search(queryBoxes.first()).size ==
            points.count { queryBoxes.first().contains(it) })
        sample.duration
    }

    println("\nBenchmark: ${points.size} points, ${queryBoxes.size} queries")
    println("Seeds: points=$pointSeed, queries=$querySeed; query widths: 20, 100, 400")
    println("Search: 5 warm-up batches, 15 measured batches (each batch runs all queries)")
    println("Median R-tree batch: ${median(treeTimes)}")
    println("Median linear scan batch: ${median(scanTimes)}")
    println("Construction: 2 warm-up builds, 7 measured builds")
    println("Median R-tree construction: ${median(buildTimes)}")
    println("All query results match ($expectedMatches total matches per batch).")
}

private fun getResult(boundingBox: BoundingBox2D, pointList: List<Point2D>) {

    val timedBuild = measureTimedValue {
        val tree = RTree2D<Point2D>()
        pointList.forEach { tree.insert(it, it) }
        tree
    }
    val rTree2D = timedBuild.value

    val timedSearch = measureTimedValue {
        rTree2D.search(boundingBox)
    }
    val rTreeResult = timedSearch.value

    val timedScan = measureTimedValue {
        pointList.filter { boundingBox.contains(it) }
    }
    val linearResults = timedScan.value

    val resultsMatch =
        rTreeResult.groupingBy { it }.eachCount() ==
                linearResults.groupingBy { it }.eachCount()


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
        prompt = "Results match: $resultsMatch",
        background = CLIColors.BG_WHITE,
    )
    printStyledPrompt(
        color = CLIColors.KOTLIN_PURPLE_BOLD,
        prompt = "R-tree search time: ${timedSearch.duration}",
        background = CLIColors.BG_WHITE,
    )
    printStyledPrompt(
        color = CLIColors.KOTLIN_PURPLE_BOLD,
        prompt = "Linear scan time: ${timedScan.duration}",
        background = CLIColors.BG_WHITE,
    )
    printStyledPrompt(
        color = CLIColors.KOTLIN_PURPLE_BOLD,
        prompt = "R-tree construction time: ${timedBuild.duration}",
        background = CLIColors.BG_WHITE,
    )
}
