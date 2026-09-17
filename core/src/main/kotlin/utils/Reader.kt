package utils

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