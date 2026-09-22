package utils

import model.LinearRing2D
import model.Point2D
import model.Segment2D

fun readDouble(prompt: String): Double {
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

fun readInt(prompt: String): Int {
    while (true) {
        print(prompt)
        val input = readlnOrNull()?.trim()
        val value = input?.toIntOrNull()

        if (value != null) {
            return value
        }

        println("Invalid value. Try again.")
    }
}

fun readString(prompt: String): String {
    while (true) {
        print(prompt)
        val input = readlnOrNull()?.trim()

        if (input != null) {
            return input
        }
    }
}

fun readBoolean(prompt: String): Boolean {
    while (true) {
        print(prompt)
        val input = readlnOrNull()?.trim()

        val inputFalse = input?.lowercase() == "n"
        val inputTrue = input?.lowercase() == "y"

        if (input != null && !inputFalse && !inputTrue) {
            println("Invalid value. Just type 'y' or 'n'")
        } else {
            return !inputFalse
        }
    }
}

fun readPointList(
    prompt: String,
    isMoreThanOne: Boolean = true,
    minPoints: Int = 2): List<Point2D> {
    while (true) {
        print(prompt)
        val input = readlnOrNull()?.trim()
        val values = input?.split(',')?.map { it.trim() }?.filter { it.isNotEmpty() }

        if (values.isNullOrEmpty()) {
            println("Invalid value. Try again.")
            continue
        }

        if (isMoreThanOne && values.size == 1) {
            println("Please provide at least $minPoints points.")
            continue
        }

        val pointList = parsePoints(values)

        if (pointList.isNotEmpty() && pointList.size == values.size) return pointList

        println("Invalid format. Expected format: 'x1 y1, x2 y2...'. Try again.")
    }
}

fun readSegment(prompt: String): Segment2D {
    while (true) {
        print(prompt)
        val input = readlnOrNull()?.trim()
        val values = input?.split(',')?.map { it.trim() }?.filter { it.isNotEmpty() }

        if (values.isNullOrEmpty()) {
            println("Invalid value. Try again.")
            continue
        }

        if (values.size < 2) {
            println("Please provide two points for the segment, the start point and the end point.")
            continue
        }

        val pointList = parsePoints(values)

        if (pointList.isNotEmpty() && pointList.size == values.size) return Segment2D(pointList.first(), pointList.last())

        println("Invalid format. Expected format: 'x1 y1, x2 y2'. Try again.")
    }
}

fun readLinearRing(prompt: String): LinearRing2D {
    while (true) {
        print(prompt)
        val input = readlnOrNull()?.trim()
        val values = input?.split(',')?.map { it.trim() }?.filter { it.isNotEmpty() }

        if (values.isNullOrEmpty()) {
            println("Invalid value. Try again.")
            continue
        }

        if (values.size < 3) {
            println("Please provide at least 3 points to make a linear ring.")
            continue
        }

       val pointList = parsePoints(values)

        if (pointList.size == values.size && pointList.size >= 3) return LinearRing2D(pointList)
        println("Please provide at least 3 points to make a linear ring using the correct format: 'x1 y1, x2 y2, x3 y3'.")
    }
}

private fun parsePoints(rawList: List<String>): List<Point2D> {
    val pointList = mutableListOf<Point2D>()
    rawList.forEach { rawList ->
        val coords = rawList.split(" ")
        if (coords.size == 2) {
            val x = coords[0].toDoubleOrNull()
            val y = coords[1].toDoubleOrNull()
            if (x != null && y != null) pointList.add(Point2D(x, y))
        }
    }

    return pointList
}