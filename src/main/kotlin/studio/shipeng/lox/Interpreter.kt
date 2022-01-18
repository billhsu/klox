package studio.shipeng.lox

internal class Interpreter : Expr.Visitor<Any?>, Stmt.Visitor<Void?> {
    private val globals = Environment()
    private var environment = globals
    private val locals = mutableMapOf<Expr, Int>()

    init {
        globals.define("clock", object : LoxCallable {
            override fun arity(): Int {
                return 0
            }

            override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
                return System.currentTimeMillis().toDouble() / 1000.0
            }
        })
    }

    fun interpret(statements: List<Stmt?>) {
        try {
            for (statement in statements) {
                if (statement != null) {
                    execute(statement)
                }
            }
        } catch (error: RuntimeError) {
            Lox.runtimeError(error)
        }
    }

    private fun evaluate(expr: Expr): Any? {
        return expr.accept(this)
    }

    private fun execute(stmt: Stmt) {
        stmt.accept(this)
    }

    fun resolve(expr: Expr, depth: Int) {
        locals[expr] = depth
    }

    fun executeBlock(
        statements: List<Stmt?>,
        environment: Environment
    ) {
        val previous = this.environment
        try {
            this.environment = environment
            for (statement in statements) {
                if (statement != null) {
                    execute(statement)
                }
            }
        } finally {
            this.environment = previous
        }
    }

    override fun visitBlockStmt(stmt: Stmt.Block): Void? {
        executeBlock(stmt.statements, Environment(environment))
        return null
    }

    override fun visitClassStmt(stmt: Stmt.Class): Void? {
        var superclass: Any? = null
        if (stmt.superclass != null) {
            superclass = evaluate(stmt.superclass)
            if (superclass !is LoxClass) {
                throw RuntimeError(
                    stmt.superclass.name,
                    "Superclass must be a class."
                )
            }
        }

        environment.define(stmt.name.lexeme, null)
        if (stmt.superclass != null) {
            environment = Environment(environment)
            environment.define("super", superclass)
        }
        val methods: MutableMap<String, LoxFunction> = HashMap()
        for (method in stmt.methods) {
            val function = LoxFunction(method, environment, method.name.lexeme == "init")
            methods[method.name.lexeme] = function
        }

        val klass = LoxClass(
            stmt.name.lexeme,
            superclass as LoxClass?, methods
        )
        if (superclass != null) {
            environment = environment.enclosing!!
        }
        environment.assign(stmt.name, klass)
        return null
    }

    override fun visitExpressionStmt(stmt: Stmt.Expression): Void? {
        evaluate(stmt.expression)
        return null
    }

    override fun visitFunctionStmt(stmt: Stmt.Function): Void? {
        val function = LoxFunction(
            stmt, environment,
            false
        )
        environment.define(stmt.name.lexeme, function)
        return null
    }

    override fun visitIfStmt(stmt: Stmt.If): Void? {
        if (isTruthy(evaluate(stmt.condition))) {
            execute(stmt.thenBranch)
        } else if (stmt.elseBranch != null) {
            execute(stmt.elseBranch)
        }
        return null
    }

    override fun visitPrintStmt(stmt: Stmt.Print): Void? {
        val value = evaluate(stmt.expression)
        println(stringify(value))
        return null
    }

    override fun visitReturnStmt(stmt: Stmt.Return): Void? {
        var value: Any? = null
        if (stmt.value != null) value = evaluate(stmt.value)
        throw Return(value!!)
    }

    override fun visitVarStmt(stmt: Stmt.Var): Void? {
        var value: Any? = null
        if (stmt.initializer != null) {
            value = evaluate(stmt.initializer)
        }
        environment.define(stmt.name.lexeme, value)
        return null
    }

    override fun visitWhileStmt(stmt: Stmt.While): Void? {
        while (isTruthy(evaluate(stmt.condition))) {
            execute(stmt.body)
        }
        return null
    }

    override fun visitAssignExpr(expr: Expr.Assign): Any? {
        val value = evaluate(expr.value)
        val distance = locals[expr]
        if (distance != null) {
            environment.assignAt(distance, expr.name, value)
        } else {
            globals.assign(expr.name, value)
        }

        return value
    }

    override fun visitBinaryExpr(expr: Expr.Binary): Any? {
        val left = evaluate(expr.left)
        val right = evaluate(expr.right) // [left]
        when (expr.operator.type) {
            TokenType.BANG_EQUAL -> return !isEqual(left, right)
            TokenType.EQUAL_EQUAL -> return isEqual(left, right)
            TokenType.GREATER -> {
                //> check-greater-operand
                checkNumberOperands(expr.operator, left, right)
                //< check-greater-operand
                return left as Double > right as Double
            }
            TokenType.GREATER_EQUAL -> {
                //> check-greater-equal-operand
                checkNumberOperands(expr.operator, left, right)
                //< check-greater-equal-operand
                return left as Double >= right as Double
            }
            TokenType.LESS -> {
                checkNumberOperands(expr.operator, left, right)
                return (left as Double) < (right as Double)
            }
            TokenType.LESS_EQUAL -> {
                checkNumberOperands(expr.operator, left, right)
                return left as Double <= right as Double
            }
            TokenType.MINUS -> {
                checkNumberOperands(expr.operator, left, right)
                return left as Double - right as Double
            }
            TokenType.PLUS -> {
                if (left is Double && right is Double) {
                    return left + right
                }
                if (left is String && right is String) {
                    return left + right
                }
                throw RuntimeError(
                    expr.operator,
                    "Operands must be two numbers or two strings."
                )
            }
            TokenType.SLASH -> {
                checkNumberOperands(expr.operator, left, right)
                return left as Double / right as Double
            }
            TokenType.STAR -> {
                checkNumberOperands(expr.operator, left, right)
                return left as Double * right as Double
            }
        }

        return null
    }

    override fun visitCallExpr(expr: Expr.Call): Any? {
        val callee = evaluate(expr.callee)
        val arguments: MutableList<Any?> = ArrayList()
        for (argument in expr.arguments) { // [in-order]
            arguments.add(evaluate(argument))
        }

        if (callee !is LoxCallable) {
            throw RuntimeError(
                expr.paren,
                "Can only call functions and classes."
            )
        }

        val function = callee
        if (arguments.size != function.arity()) {
            throw RuntimeError(
                expr.paren, "Expected " +
                        function.arity() + " arguments but got " +
                        arguments.size + "."
            )
        }

        return function.call(this, arguments)
    }

    override fun visitGetExpr(expr: Expr.Get): Any? {
        val instance = evaluate(expr.instance)
        if (instance is LoxInstance) {
            return instance[expr.name]
        }
        throw RuntimeError(
            expr.name,
            "Only instances have properties."
        )
    }

    override fun visitGroupingExpr(expr: Expr.Grouping): Any? {
        return evaluate(expr.expression)
    }

    override fun visitLiteralExpr(expr: Expr.Literal): Any? {
        return expr.value
    }

    override fun visitLogicalExpr(expr: Expr.Logical): Any? {
        val left = evaluate(expr.left)
        if (expr.operator.type === TokenType.OR) {
            if (isTruthy(left)) return left
        } else {
            if (!isTruthy(left)) return left
        }
        return evaluate(expr.right)
    }

    override fun visitSetExpr(expr: Expr.Set): Any? {
        val instance = evaluate(expr.instance) as? LoxInstance
            ?: throw RuntimeError(
                expr.name,
                "Only instances have fields."
            )
        val value = evaluate(expr.value)
        instance[expr.name] = value
        return value
    }

    override fun visitSuperExpr(expr: Expr.Super): Any {
        val distance = locals[expr]!!
        val superclass = environment.getAt(
            distance, "super"
        ) as LoxClass
        val instance = environment.getAt(
            distance - 1, "this"
        ) as LoxInstance
        val method = superclass.findMethod(expr.method.lexeme)
            ?: throw RuntimeError(
                expr.method,
                "Undefined property '" + expr.method.lexeme + "'."
            )
        return method.bind(instance)
    }

    override fun visitThisExpr(expr: Expr.This): Any? {
        return lookUpVariable(expr.keyword, expr)
    }

    override fun visitUnaryExpr(expr: Expr.Unary): Any? {
        val right = evaluate(expr.right)
        when (expr.operator.type) {
            TokenType.BANG -> return !isTruthy(right)
            TokenType.MINUS -> {
                checkNumberOperand(expr.operator, right)
                return -(right as Double)
            }
        }

        return null
    }

    override fun visitVariableExpr(expr: Expr.Variable): Any? {
        return lookUpVariable(expr.name, expr)
    }

    private fun lookUpVariable(name: Token, expr: Expr): Any? {
        val distance = locals[expr]
        return if (distance != null) {
            environment.getAt(distance, name.lexeme)
        } else {
            globals.get(name)
        }
    }

    private fun checkNumberOperand(operator: Token, operand: Any?) {
        if (operand is Double) return
        throw RuntimeError(operator, "Operand must be a number.")
    }

    private fun checkNumberOperands(
        operator: Token,
        left: Any?, right: Any?
    ) {
        if (left is Double && right is Double) return
        throw RuntimeError(operator, "Operands must be numbers.")
    }

    private fun isTruthy(instance: Any?): Boolean {
        if (instance == null) return false
        return if (instance is Boolean) instance else true
    }

    private fun isEqual(a: Any?, b: Any?): Boolean {
        if (a == null && b == null) return true
        return if (a == null) false else a == b
    }

    private fun stringify(instance: Any?): String {
        if (instance == null) return "nil"
        if (instance is Double) {
            var text = instance.toString()
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length - 2)
            }
            return text
        }
        return instance.toString()
    }
}