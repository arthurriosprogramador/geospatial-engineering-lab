import area.area
import area.isCounterClockwise
import formatter.formatNumber
import model.LinearRing2D
import model.Polygon2D
import spatial.contains
import utils.*

private const val pointsPrompt = "Please type the point coordinates separated by space and use comma to " +
        "separate points (E.g.: 0 0, 3 4): "
private const val singlePointPrompt = "Please type the point coordinates separated by comma (E.g.: 3 4): "
private const val segmentPrompt =
    "Please type the start and final segment point coordinates separated by space and use comma to " +
            "separate points (E.g.: 0 0, 3 4): "
private const val hasHolePrompt = "Does the polygon have holes? (y/n): "
private const val exteriorRingPrompt =
    "Please type at least 3 points to make an exterior ring. The point coordinates " +
            "separated by space and use comma to separate points (E.g.: 0 0, 3 4): "
private const val holesQuantityPrompt = "Please type how many holes the polygon has: "
private const val holePrompt =
    "Please type at least 3 points to make an interior ring. The point coordinates should be " +
            "separated by space and use comma to separate points (E.g.: 0 0, 3 4): "

fun main() {
    startProgram()
}

private fun startProgram() {
    println("------------------------------------------------------------")
    println("=== Hi, welcome to Vector Geometry/Spatial Analysis ===")
    println("------------------------------------------------------------")

    val options = listOf(
        "Distance & Displacement Vector",
        "Segment Length & Point Orientation (Left/Right/Collinear)",
        "Ring/Polygon Area (Shoelace & Winding Order)",
        "Point-in-Polygon Containment (Ray Casting)",
        "Exit"
    )

    while (true) {
        printOptions(options)
        val selectedOption = selectOption(options)

        if (selectedOption == 5) {
            println("\nExiting program, farewell!")
            break
        }

        handleOperationOption(selectedOption)
    }
}

private fun printOptions(options: List<String>) {
    options.forEachIndexed { index, option ->
        println("${index + 1} - $option")
    }
    println("\nPlease choose the desired option: ")
}

private fun selectOption(options: List<String>): Int {
    while (true) {
        val input = readlnOrNull()?.trim()
        val option = input?.toIntOrNull()

        if (option != null && option in 1..options.size) {
            return option
        } else {
            println("\nInvalid option, please type a valid option!\n")
            printOptions(options)
        }
    }
}

private fun handleOperationOption(option: Int) {
    when (option) {
        1 -> calculateDistanceAndDisplacementVector()
        2 -> calculateSegmentLengthAndPointOrientation()
        3 -> calculateRingPolygonArea()
        4 -> calculatePointInPolygonContainment()
    }
}

private fun calculateDistanceAndDisplacementVector() {
    println("------------------------------------------------------------------------------------------------------------------------")
    val points = readPointList(pointsPrompt)
    println("------------------------------------------------------------------------------------------------------------------------")

    var totalDistance = 0.0
    points.forEachIndexed { index, point ->
        if (index < points.size - 1) {
            val nextPoint = points[index + 1]
            val distance = point.distanceTo(nextPoint)
            println("Distance from P$index to P${index + 1}: ${distance.formatNumber()}")
            totalDistance += distance
        }
    }

    val vector = points.last() - points.first()

    println("\nCumulative distance: ${totalDistance.formatNumber()}\nVector: (${vector.dx}, ${vector.dy})\n")
}

private fun calculateSegmentLengthAndPointOrientation() {
    println("------------------------------------------------------------------------------------------------------------------------")
    val segment = readSegment(segmentPrompt)
    println("------------------------------------------------------------------------------------------------------------------------")
    val point = readPointList(singlePointPrompt, false).first()
    println("------------------------------------------------------------------------------------------------------------------------")

    val segmentLength = segment.length()
    val crossProduct = segment.orientationOf(point)
    val orientationLabel = when {
        crossProduct > 0.0 -> "Counter-Clockwise curve"
        crossProduct < 0.0 -> "Clockwise curve"
        else -> "Collinear"
    }

    println(
        "\nThe segment length is $segmentLength and the cross product is $crossProduct, which means ${orientationLabel}\n"
    )
}

private fun calculateRingPolygonArea() {
    val polygon = readPolygon()

    println("Exterior ring has ${polygon.exteriorRing.area()}m²")
    if (polygon.interiorRings.isNotEmpty()) {
        println("The polygon has ${polygon.interiorRings.size} holes, each of them with: ")
        for (hole in polygon.interiorRings) {
            println("${hole.area()}m²")
        }
    }
    val signedAreaLabel = if (polygon.exteriorRing.isCounterClockwise()) "Counter-Clockwise" else "Clockwise"
    println("The polygon has ${polygon.area()}m² and its exterior ring is ${signedAreaLabel}.\n")
}

private fun calculatePointInPolygonContainment() {
    val polygon = readPolygon()

    val point = readPointList(singlePointPrompt, false).first()
    println("------------------------------------------------------------------------------------------------------------------------")

    val isInside = polygon.contains(point)
    val containmentLabel = if (isInside) "inside" else "outside"
    if (isInside) {
        println("The point is $containmentLabel of the polygon.\n")
    } else {
        var holesWithCollisionQuantity = 0
        polygon.interiorRings.forEach {
            if (it.contains(point)) holesWithCollisionQuantity++
        }
        println("The point is $containmentLabel of the polygon and the point collides with $holesWithCollisionQuantity hole(s).\n")
    }
}

fun readPolygon(): Polygon2D {
    println("------------------------------------------------------------------------------------------------------------")
    val exteriorRing = readLinearRing(exteriorRingPrompt)
    println("------------------------------------------------------------------------------------------------------------")
    val hasHoles = readBoolean(hasHolePrompt)
    println("------------------------------------------------------------------------------------------------------------")

    val holes = mutableListOf<LinearRing2D>()

    if (hasHoles) {
        val count = readInt(holesQuantityPrompt)
        println("------------------------------------------------------------------------------------------------------------")
        for (i in 0 until count) {
            holes.add(readLinearRing(holePrompt))
        }
        println("------------------------------------------------------------------------------------------------------------")
    }
    return Polygon2D(exteriorRing, holes)
}

