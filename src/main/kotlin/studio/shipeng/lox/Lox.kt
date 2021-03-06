package studio.shipeng.lox

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.system.exitProcess

class Lox {
    companion object {
        private const val ANSI_RESET = "\u001B[0m"
        private const val ANSI_RED = "\u001B[31m"
        private const val ANSI_YELLOW = "\u001B[33m"
        private const val ANSI_BLUE = "\u001B[34m"

        private var hadError = false
        private var hadRuntimeError = false
        private val interpreter = Interpreter()
        private val astPrinter = AstPrinter()
        private var promptMode = false

        @JvmStatic
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

        internal fun error(line: Int, message: String) {
            report(line, "", message)
        }

        internal fun report(line: Int, where: String, message: String) {
            System.err.println(
                "[line $line] Error$where: $message"
            )
            hadError = true
        }

        internal fun error(token: Token, message: String) {
            if (token.type === TokenType.EOF) {
                report(token.line, " at end", message)
            } else {
                report(token.line, " at '" + token.lexeme + "'", message)
            }
        }

        internal fun runtimeError(error: RuntimeError) {
            print(ANSI_RED)
            System.err.println(
                error.message + "\n[line " + error.token.line + "]"
            )
            print(ANSI_RESET)
            hadRuntimeError = true
        }

        @Throws(IOException::class)
        internal fun runFile(path: String) {
            val bytes = Files.readAllBytes(Paths.get(path))
            run(String(bytes, Charset.defaultCharset()))

            // Indicate an error in the exit code.
            if (hadError) exitProcess(65)
            if (hadRuntimeError) exitProcess(70)
        }

        @Throws(IOException::class)
        internal fun runPrompt() {
            promptMode = true
            val input = InputStreamReader(System.`in`)
            val reader = BufferedReader(input)
            System.setErr(System.out)

            while (true) {
                print("$ANSI_YELLOW>$ANSI_RESET ")
                run(reader.readLine())
            }
        }

        private fun run(source: String) {
            hadError = false
            hadRuntimeError = false
            val scanner = Scanner(source)
            val tokens: List<Token> = scanner.scanTokens()
            val parser = Parser(tokens)
            val statements = parser.parse()
            if (promptMode) {
                if (statements.isNotEmpty()) {
                    print("$ANSI_BLUE[AST]: ")
                    statements.forEach { println(astPrinter.print(it)) }
                    print(ANSI_RESET)
                }
            }
            if (hadError) {
                return
            }
            val resolver = Resolver(interpreter)
            resolver.resolve(statements)
            if (hadError) {
                return
            }
            interpreter.interpret(statements);
        }
    }
}