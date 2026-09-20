package model

import kotlin.math.sqrt

data class Vector2D(
    val dx : Double,
    val dy : Double
) {
    /**
     * # Vector Magnitude (Euclidean Length)
     *
     * Calculates the scalar length of the vector using the Euclidean norm (L₂ norm).
     * Conceptually, this corresponds to applying the Pythagorean theorem:
     *
     * `magnitude = √(dx² + dy²)`
     *
     * When applied to a displacement vector between two points, this returns
     * the direct Euclidean distance between them in meters.
     *
     * @return The non-negative geometric length of the vector.
     */
    fun magnitude() : Double = sqrt(dx * dx + dy * dy)

    /**
     * # Dot Product
     *
     * Used to know the projection, angle between vectors and orthogonality.
     * If the product is equal to 0, the vectors are perpendicular.
     *
     * @param vector the secondary vector to calculate the product against.
     * @return The scalar values representing the dot product
     */
    infix fun dot(vector : Vector2D) : Double = (dx * vector.dx) + (dy * vector.dy)

    /**
     * # Cross Product
     *
     * Used to know the parallelogram area magnitude made by two signed vectors:
     *
     * `> 0` : Curve to the **LEFT (Counter-Clockwise)**
     *
     * `< 0` : Curve to the **RIGHT (Clockwise)**
     *
     * `== 0` : **Collinear (points on the same direction or opposite directions)**
     *
     * @param vector - the secondary vector to calculate the cross product against.
     * @return The scalar value representing the cross product.
     */
    infix fun cross(vector: Vector2D) : Double = (dx * vector.dy) - (dy * vector.dx)
}
