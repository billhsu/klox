package studio.shipeng.lox


sealed class Stmt {
    internal interface Visitor<R> {
        fun visitBlockStmt(stmt: Block): R
        fun visitClassStmt(stmt: Class): R
        fun visitExpressionStmt(stmt: Expression): R
        fun visitFunctionStmt(stmt: Function): R
        fun visitIfStmt(stmt: If): R
        fun visitPrintStmt(stmt: Print): R
        fun visitReturnStmt(stmt: Return): R
        fun visitVarStmt(stmt: Var): R
        fun visitWhileStmt(stmt: While): R
    }

    internal abstract fun <R> accept(visitor: Visitor<R>): R

    class Block(val statements: List<Stmt?>) : Stmt() {
        override fun <R> accept(visitor: Visitor<R>): R = visitor.visitBlockStmt(this)
    }

    class Class(
        val name: Token,
        val superclass: Expr.Variable?,
        val methods: List<Function>
    ) : Stmt() {
        override fun <R> accept(visitor: Visitor<R>): R = visitor.visitClassStmt(this)
    }

    class Expression(val expression: Expr) : Stmt() {
        override fun <R> accept(visitor: Visitor<R>): R = visitor.visitExpressionStmt(this)
    }

    class Function(val name: Token, val params: List<Token>, val body: List<Stmt?>) :
        Stmt() {
        override fun <R> accept(visitor: Visitor<R>): R = visitor.visitFunctionStmt(this)
    }

    class If(val condition: Expr, val thenBranch: Stmt, val elseBranch: Stmt?) : Stmt() {
        override fun <R> accept(visitor: Visitor<R>): R = visitor.visitIfStmt(this)
    }

    class Print(val expression: Expr) : Stmt() {
        override fun <R> accept(visitor: Visitor<R>): R = visitor.visitPrintStmt(this)
    }

    class Return(val keyword: Token, val value: Expr?) : Stmt() {
        override fun <R> accept(visitor: Visitor<R>): R = visitor.visitReturnStmt(this)
    }

    class Var(val name: Token, val initializer: Expr?) : Stmt() {
        override fun <R> accept(visitor: Visitor<R>): R = visitor.visitVarStmt(this)
    }

    class While(val condition: Expr, val body: Stmt) : Stmt() {
        override fun <R> accept(visitor: Visitor<R>): R = visitor.visitWhileStmt(this)
    }

}
