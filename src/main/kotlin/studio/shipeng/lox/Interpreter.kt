package studio.shipeng.lox


class Interpreter : Expr.Visitor<Any>, Stmt.Visitor<Void> {
    private val globals = Environment()
    private val environment = globals
    private val locals = mutableMapOf<Expr, Int>()
    fun resolve(expr: Expr, depth: Int) {
        locals[expr] = depth
    }

    override fun visitAssignExpr(expr: Expr.Assign): Any {
        TODO("Not yet implemented")
    }

    override fun visitBinaryExpr(expr: Expr.Binary): Any {
        TODO("Not yet implemented")
    }

    override fun visitCallExpr(expr: Expr.Call): Any {
        TODO("Not yet implemented")
    }

    override fun visitGetExpr(expr: Expr.Get): Any {
        TODO("Not yet implemented")
    }

    override fun visitGroupingExpr(expr: Expr.Grouping): Any {
        TODO("Not yet implemented")
    }

    override fun visitLiteralExpr(expr: Expr.Literal): Any {
        TODO("Not yet implemented")
    }

    override fun visitLogicalExpr(expr: Expr.Logical): Any {
        TODO("Not yet implemented")
    }

    override fun visitSetExpr(expr: Expr.Set): Any {
        TODO("Not yet implemented")
    }

    override fun visitSuperExpr(expr: Expr.Super): Any {
        TODO("Not yet implemented")
    }

    override fun visitThisExpr(expr: Expr.This): Any {
        TODO("Not yet implemented")
    }

    override fun visitUnaryExpr(expr: Expr.Unary): Any {
        TODO("Not yet implemented")
    }

    override fun visitVariableExpr(expr: Expr.Variable): Any {
        TODO("Not yet implemented")
    }

    override fun visitBlockStmt(stmt: Stmt.Block): Void {
        TODO("Not yet implemented")
    }

    override fun visitClassStmt(stmt: Stmt.Class): Void {
        TODO("Not yet implemented")
    }

    override fun visitExpressionStmt(stmt: Stmt.Expression): Void {
        TODO("Not yet implemented")
    }

    override fun visitFunctionStmt(stmt: Stmt.Function): Void {
        TODO("Not yet implemented")
    }

    override fun visitIfStmt(stmt: Stmt.If): Void {
        TODO("Not yet implemented")
    }

    override fun visitPrintStmt(stmt: Stmt.Print): Void {
        TODO("Not yet implemented")
    }

    override fun visitReturnStmt(stmt: Stmt.Return): Void {
        TODO("Not yet implemented")
    }

    override fun visitVarStmt(stmt: Stmt.Var): Void {
        TODO("Not yet implemented")
    }

    override fun visitWhileStmt(stmt: Stmt.While): Void {
        TODO("Not yet implemented")
    }

}