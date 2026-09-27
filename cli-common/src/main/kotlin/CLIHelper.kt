import org.jline.terminal.Terminal
import org.jline.terminal.TerminalBuilder
import org.jline.utils.NonBlockingReader

enum class MenuAction {
    UP,
    DOWN,
    SELECT,
    IGNORE
}

private fun readMenuAction(reader: NonBlockingReader) : MenuAction {
    val firstChar = reader.read()
    if (firstChar == -1) return MenuAction.IGNORE

    if (firstChar == 10 || firstChar == 13) return MenuAction.SELECT

    if (firstChar == 27) {
        val secondChar = reader.read()
        if (secondChar == '['.code || secondChar == 'O'.code) {
            return when (reader.read()) {
                'A'.code -> MenuAction.UP
                'B'.code -> MenuAction.DOWN
                else -> MenuAction.IGNORE
            }
        }
    }

    return MenuAction.IGNORE
}

fun createCLIMenu(options: List<String>, title: String) : Int {
    val ansiReset = "\u001b[0m"
    val ansiWhiteBold = "\u001b[1;37m"
    val kotlinPurple = "\u001b[38;2;127;82;255;1m"

    val terminal: Terminal = try {
        TerminalBuilder.builder().system(true).dumb(false).build()
    } catch (_: Exception) {
        return createFallbackMenu(options, title)
    }

    val previousAttributes = terminal.enterRawMode()
    val reader = terminal.reader()
    val out = terminal.writer()

    val pointerIcon = "►"
    val arrowsIcon = "↑ / ↓"
    val canEncode = terminal.encoding().newEncoder()
    val pointer = if (canEncode.canEncode(pointerIcon)) pointerIcon else ">"
    val arrows = if (canEncode.canEncode(arrowsIcon.replace("/", "").replace(" ", ""))) {
        arrowsIcon
    } else "Up / Down"

    var selectedIndex = 0
    var isFirstRender = true

    try {
        out.print("\u001b[?25l")

        while (true) {
            if (!isFirstRender) {
                val totalLinesToRewind = options.size + 3
                out.print("\u001b[${totalLinesToRewind}A")
            }
            isFirstRender = false

            out.print("\u001b[2K")
            out.println(title)
            out.print("\u001b[2K")
            out.println("Navigate with $arrows. Press Enter to choose:\n")

            options.forEachIndexed { i, option ->
                out.print("\u001b[2K")
                if (i == selectedIndex) {
                    out.println("$kotlinPurple  $pointer $option$ansiReset")
                } else {
                    out.println("    $ansiWhiteBold$option$ansiReset")
                }
            }
            out.flush()

            when (readMenuAction(reader)) {
                MenuAction.UP -> selectedIndex = if (selectedIndex > 0) selectedIndex - 1 else options.lastIndex
                MenuAction.DOWN -> selectedIndex = if (selectedIndex < options.lastIndex) selectedIndex + 1 else 0
                MenuAction.SELECT -> return selectedIndex + 1
                MenuAction.IGNORE -> Unit
            }
        }
    } finally {
        out.print("\u001b[?25h")
        out.println()
        out.flush()
        terminal.attributes = previousAttributes
        terminal.close()
    }
}

fun createFallbackMenu(options: List<String>, title: String): Int {
    while (true) {
        println(title)

        options.forEachIndexed { i, option ->
            println("${i + 1}. $option")
        }

        print("\nPlease select an option by typing its number and pressing Enter: ")

        val input = readlnOrNull()?.trim()
        val option = input?.toIntOrNull()

        if (option != null && option in 1..options.size) return option

        println("\n\u001b[31mInvalid option. Please enter a valid number.\u001b[0m\n")

    }
}

fun printTitle(title: String) {
    title.let {
        it.printDividerByPrompt()
        println(it)
        it.printDividerByPrompt()
    }
}

fun printSameLinePrompt(prompt: String) {
    prompt.let {
        it.printDividerByPrompt()
        println(it)
        it.printDividerByPrompt()
    }
}


fun printFarewell() {
    println("Exiting application. Goodbye!")
}

fun String.printDividerByPrompt() {
    val divider = "-".repeat(this.length + 10)
    println(divider)
}
