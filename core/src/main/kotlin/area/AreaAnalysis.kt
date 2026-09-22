package area

import model.LinearRing2D
import model.Polygon2D
import kotlin.math.abs

/**
 * It calculates the linear ring signed area using the Shoelace formula (Green's theorem).
 *
 * The sign determines the vertex orientation:
 *
 * `> 0.0` -> Counter-Clockwise
 *
 * `< 0.0` -> Clockwise
 *
 * `== 0.0` -> Collinear
 */
fun LinearRing2D.signedArea() : Double {
    val verticesSize = vertices.size
    var sum = 0.0

    for (i in 0 until verticesSize) {
        val current = vertices[i]
        val next = vertices[(i + 1) % verticesSize]

        sum += (current.x * next.y) - (current.y * next.x)
    }

    return sum / 2.0
}

/**
 * @return The linear ring absolute metric area.
 */
fun LinearRing2D.area() : Double = abs(signedArea())

/**
 * Verifies if the ring vertices are ordered in a counter-clockwise direction.
 */
fun LinearRing2D.isCounterClockwise() : Boolean = signedArea() > 0.0

/**
 * Calculates net area of a polygon supporting holes:
 *
 * Net Area = Exterior Area - Sum of Inner Areas
 */
fun Polygon2D.area() : Double {
    val exteriorArea = exteriorRing.area()
    val holesArea = interiorRings.sumOf { it.area() }

    return (exteriorArea - holesArea).coerceAtLeast(0.0)
}