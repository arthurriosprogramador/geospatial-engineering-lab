enum class CLIColors(val ansiCode: String) {
    // Reset Style & Color
    RESET("\u001b[0m"),

    // Foreground Text Colors
    WHITE_BOLD("\u001b[1;37m"),
    GRAY("\u001b[90m"),
    KOTLIN_PURPLE("\u001b[38;2;127;82;255m"),
    KOTLIN_PURPLE_BOLD("\u001b[1;38;2;127;82;255m"),
    GREEN_BOLD("\u001b[1;32m"),
    RED_BOLD("\u001b[1;31m"),
    CYAN("\u001b[36m"),

    // Background Colors
    BG_KOTLIN_PURPLE("\u001b[48;2;127;82;255m"),
    BG_WHITE("\u001b[47m"),
    BG_BRIGHT_WHITE("\u001b[107m");

    override fun toString(): String = ansiCode
}