import org.jline.terminal.Terminal
import org.jline.terminal.TerminalBuilder
import org.jline.utils.NonBlockingReader
import java.io.EOFException
import java.io.PrintWriter

fun PrintWriter.hideCursor(): PrintWriter = apply {
    print("\u001b[?25l")
}

fun PrintWriter.showCursor(): PrintWriter = apply {
    print("\u001b[?25h")
    flush()
}

fun PrintWriter.clearLine(): PrintWriter = apply {
    print("\u001b[2K")
    flush()
}

fun PrintWriter.clearScreen(): PrintWriter = apply {
    print("\u001b[H\u001b[2J")
    flush()
}

fun PrintWriter.rewindCursor(lines: Int): PrintWriter = apply {
    if (lines > 0) {
        print("\u001b[${lines}A")
        flush()
    }
}

fun PrintWriter.resetStyle(): PrintWriter = apply {
    print(CLIColors.RESET)
    flush()
}

fun printStyledPrompt(
    color: CLIColors,
    prompt: String,
    background: CLIColors? = null,
    shouldBreakLine: Boolean = true,) {
    val backgroundPrefix = background ?: ""
    val styledPrompt = "$backgroundPrefix$color$prompt${CLIColors.RESET}"

    if (shouldBreakLine) println(styledPrompt) else print(styledPrompt)
}

enum class MenuAction {
    UP,
    DOWN,
    SELECT,
    IGNORE
}

private fun readMenuAction(reader: NonBlockingReader) : MenuAction {
    val firstChar = reader.read()
    if (firstChar == -1) throw EOFException("Input ended.")

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
        out.hideCursor()

        while (true) {
            if (!isFirstRender) {
                val totalLinesToRewind = options.size + 4
                out.rewindCursor(totalLinesToRewind)
            }
            isFirstRender = false

            out.clearLine()
            printTitle(title)
            out.clearLine().println("Navigate with $arrows. Press Enter to choose:")

            options.forEachIndexed { i, option ->
                out.clearLine()
                if (i == selectedIndex) {
                    out.println("${CLIColors.BG_WHITE}${CLIColors.KOTLIN_PURPLE}  $pointer $option${CLIColors.RESET}")
                } else {
                    out.println("    ${CLIColors.WHITE_BOLD}$option${CLIColors.RESET}")
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
        out.resetStyle()
        out.showCursor()
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

        val input = readlnOrNull() ?: throw EOFException("Input ended.")
        val option = input.trim().toIntOrNull()

        if (option != null && option in 1..options.size) return option

        println("\n\u001b[31mInvalid option. Please enter a valid number.\u001b[0m\n")

    }
}

fun printTitle(title: String) {
    title.let {
        it.printDividerByPrompt()
        printStyledPrompt(
            color = CLIColors.WHITE_BOLD,
            background = CLIColors.KOTLIN_PURPLE,
            prompt = it,
        )
        it.printDividerByPrompt()
    }
}

fun printSameLinePrompt(prompt: String) {
    prompt.let {
        it.printDividerByPrompt()
        print(it)
    }
}


fun printFarewell() {
    println("Exiting application. Goodbye!")
}

fun String.printDividerByPrompt() {
    val divider = "-".repeat(this.length + 10)
    println(divider)
}
