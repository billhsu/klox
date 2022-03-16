package studio.shipeng.lox

import studio.shipeng.lox.Expr.*
import studio.shipeng.lox.Expr.Set
import studio.shipeng.lox.Stmt.Return
import studio.shipeng.lox.Stmt.While

internal class AstPrinter : Visitor<String>, Stmt.Visitor<String> {
    fun print(expr: Expr): String {
        return expr.accept(this)
    }

    fun print(stmt: Stmt): String {
        return stmt.accept(this)
    }

    override fun visitBlockStmt(stmt: Stmt.Block): String {
        val builder = StringBuilder()
        builder.append("(block ")
        for (statement in stmt.statements) {
            if (statement != null) {
                builder.append(statement.accept(this))
            }
        }
        builder.append(")")
        return builder.toString()
    }

    override fun visitClassStmt(stmt: Stmt.Class): String {
        val builder = StringBuilder()
        builder.append("(class " + stmt.name.lexeme)
        if (stmt.superclass != null) {
            builder.append(" < " + print(stmt.superclass))
        }
        for (method in stmt.methods) {
            builder.append(" " + print(method))
        }
        builder.append(")")
        return builder.toString()
    }

    override fun visitExpressionStmt(stmt: Stmt.Expression): String {
        return parenthesize(";", stmt.expression)
    }

    override fun visitFunctionStmt(stmt: Stmt.Function): String {
        val builder = StringBuilder()
        builder.append("(fun " + stmt.name.lexeme + "(")
        for (param in stmt.params) {
            if (param != stmt.params[0]) builder.append(" ")
            builder.append(param.lexeme)
        }
        builder.append(") ")
        for (body in stmt.body) {
            builder.append(body!!.accept(this))
        }
        builder.append(")")
        return builder.toString()
    }

    override fun visitIfStmt(stmt: Stmt.If): String {
        return if (stmt.elseBranch == null) {
            parenthesize2("if", stmt.condition, stmt.thenBranch)
        } else parenthesize2(
            "if-else", stmt.condition, stmt.thenBranch,
            stmt.elseBranch
        )
    }

    override fun visitPrintStmt(stmt: Stmt.Print): String {
        return parenthesize("print", stmt.expression)
    }

    override fun visitReturnStmt(stmt: Return): String {
        return if (stmt.value == null) "(return)" else parenthesize("return", stmt.value)
    }

    override fun visitVarStmt(stmt: Stmt.Var): String {
        return if (stmt.initializer == null) {
            parenthesize2("var", stmt.name)
        } else parenthesize2("var", stmt.name, "=", stmt.initializer)
    }

    override fun visitWhileStmt(stmt: While): String {
        return parenthesize2("while", stmt.condition, stmt.body)
    }

    override fun visitAssignExpr(expr: Assign): String {
        return parenthesize2("=", expr.name.lexeme, expr.value)
    }

    override fun visitBinaryExpr(expr: Binary): String {
        return parenthesize(
            expr.operator.lexeme,
            expr.left, expr.right
        )
    }

    override fun visitCallExpr(expr: Call): String {
        return parenthesize2("call", expr.callee, expr.arguments)
    }

    override fun visitGetExpr(expr: Get): String {
        return parenthesize2(".", expr.instance, expr.name.lexeme)
    }

    override fun visitGroupingExpr(expr: Grouping): String {
        return parenthesize("group", expr.expression)
    }

    override fun visitLiteralExpr(expr: Literal): String {
        return if (expr.value == null) "nil" else expr.value.toString()
    }

    override fun visitLogicalExpr(expr: Logical): String {
        return parenthesize(expr.operator.lexeme, expr.left, expr.right)
    }

    override fun visitSetExpr(expr: Set): String {
        return parenthesize2(
            "=",
            expr.instance, expr.name.lexeme, expr.value
        )
    }

    override fun visitSuperExpr(expr: Super): String {
        return parenthesize2("super", expr.method)
    }

    override fun visitThisExpr(expr: This): String {
        return "this"
    }

    override fun visitUnaryExpr(expr: Unary): String {
        return parenthesize(expr.operator.lexeme, expr.right)
    }

    override fun visitVariableExpr(expr: Variable): String {
        return expr.name.lexeme
    }

    override fun visitArrayExpr(expr: Expr.Array): String {
        return parenthesize2("array", expr.elements)
    }

    override fun visitGetSubscriptExpr(expr: GetSubscript): String {
        return parenthesize2("subscript", expr.instance, expr.index)
    }

    private fun parenthesize(name: String, vararg exprs: Expr): String {
        val builder = StringBuilder()
        builder.append("(").append(name)
        for (expr in exprs) {
            builder.append(" ")
            builder.append(expr.accept(this))
        }
        builder.append(")")
        return builder.toString()
    }

    private fun parenthesize2(name: String, vararg parts: Any): String {
        val builder = StringBuilder()
        builder.append("(").append(name)
        transform(builder, *parts)
        builder.append(")")
        return builder.toString()
    }

    private fun transform(builder: StringBuilder, vararg parts: Any) {
        for (part in parts) {
            builder.append(" ")
            when (part) {
                is Expr -> {
                    builder.append(part.accept(this))
                }
                is Stmt -> {
                    builder.append(part.accept(this))
                }
                is Token -> {
                    builder.append(part.lexeme)
                }
                is List<*> -> {
                    transform(builder, *(part.filterNotNull().toTypedArray()))
                }
                else -> {
                    builder.append(part)
                }
            }
        }
    }
}
