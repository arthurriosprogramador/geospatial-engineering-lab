import model.LinearRing2D
import model.Point2D
import model.Segment2D
import java.io.EOFException

fun readDouble(prompt: String): Double {
    while (true) {
        printSameLinePrompt(prompt)
        val stringInput = readlnOrNull() ?: throw EOFException("Input ended.")
        val input = stringInput.trim().replace(',', '.')
        val value = input.toDoubleOrNull()
        prompt.printDividerByPrompt()

        if (value != null && value.isFinite()) {
            return value
        }

        println("Invalid value. Try again.")
    }
}

fun readInt(prompt: String): Int {
    while (true) {
        printSameLinePrompt(prompt)
        val inputString = readlnOrNull() ?: throw EOFException("Input ended.")
        val input = inputString.trim()
        val value = input.toIntOrNull()
        prompt.printDividerByPrompt()

        if (value != null) {
            return value
        }

        println("Invalid value. Try again.")
    }
}

fun readString(prompt: String): String {
        printSameLinePrompt(prompt)
        val input = readlnOrNull() ?: throw EOFException("Input ended.")
        prompt.printDividerByPrompt()

        return input.trim()
}

fun readBoolean(prompt: String): Boolean {
    while (true) {
        print(prompt)
        val inputString = readlnOrNull() ?: throw EOFException("Input ended.")
        val input = inputString.trim()

        val inputFalse = input.lowercase() == "n"
        val inputTrue = input.lowercase() == "y"

        if (!inputFalse && !inputTrue) {
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
        val inputString = readlnOrNull() ?: throw EOFException("Input ended.")
        val input = inputString.trim()
        val values = input.split(',').map { it.trim() }.filter { it.isNotEmpty() }

        if (isMoreThanOne && values.size < minPoints) {
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
        val inputString = readlnOrNull() ?: throw EOFException("Input ended.")
        val input = inputString.trim()
        val values = input.split(',').map { it.trim() }.filter { it.isNotEmpty() }

        if (values.size != 2) {
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
        val inputString = readlnOrNull() ?: throw EOFException("Input ended.")
        val input = inputString.trim()
        val values = input.split(',').map { it.trim() }.filter { it.isNotEmpty() }

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
    rawList.forEach { rawPoint ->
        val coords = rawPoint.trim().split(Regex("\\s+"))
        if (coords.size == 2) {
            val x = coords[0].toDoubleOrNull()
            val y = coords[1].toDoubleOrNull()
            if (x != null && y != null && x.isFinite() && y.isFinite()) pointList.add(Point2D(x, y))
        }
    }

    return pointList
}