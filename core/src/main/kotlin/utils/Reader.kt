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