import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.system.exitProcess

private var hadError = false
private var hadRuntimeError = false

fun main(args: Array<String>) {
    when (args.size) {
        1 -> runFile(args[0])
        0 -> runPrompt()
        else -> {
            println("Usage: klox [script]")
            exitProcess(64)
        }
    }
}

@Throws(IOException::class)
private fun runFile(path: String) {
    val bytes = Files.readAllBytes(Paths.get(path))
    run(String(bytes, Charset.defaultCharset()))

    // Indicate an error in the exit code.
    if (hadError) exitProcess(65)
    if (hadRuntimeError) exitProcess(70)
}

@Throws(IOException::class)
private fun runPrompt() {
    val input = InputStreamReader(System.`in`)
    val reader = BufferedReader(input)

    while (true) {
        print("> ")
        run(reader.readLine())
    }
}

private fun run(source: String) {
    hadError = false
    hadRuntimeError = false
    if (hadError) {
        return
    }
}
