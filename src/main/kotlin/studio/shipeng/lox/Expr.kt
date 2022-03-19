package studio.shipeng.lox


sealed class Expr {
    internal interface Visitor<R> {
        fun visitAssignExpr(expr: Assign): R
        fun visitBinaryExpr(expr: Binary): R
        fun visitCallExpr(expr: Call): R
        fun visitGetExpr(expr: Get): R
        fun visitGroupingExpr(expr: Grouping): R
        fun visitLiteralExpr(expr: Literal): R
        fun visitLogicalExpr(expr: Logical): R
        fun visitSetExpr(expr: Set): R
        fun visitSuperExpr(expr: Super): R
        fun visitThisExpr(expr: This): R
        fun visitUnaryExpr(expr: Unary): R
        fun visitVariableExpr(expr: Variable): R
        fun visitArrayExpr(expr: Array): R
        fun visitGetSubscriptExpr(expr: GetSubscript): R
        fun visitSetSubscriptExpr(expr: SetSubscript): R
    }

    internal abstract fun <R> accept(visitor: Visitor<R>): R

    class Assign(val name: Token, val value: Expr) : Expr() {
        override fun <R> accept(visitor: Visitor<R>): R = visitor.visitAssignExpr(this)
    }

    class Binary(val left: Expr, val operator: Token, val right: Expr) : Expr() {
        override fun <R> accept(visitor: Visitor<R>): R = visitor.visitBinaryExpr(this)
    }

    class Call(val callee: Expr, val paren: Token, val arguments: List<Expr>) :
        Expr() {
        override fun <R> accept(visitor: Visitor<R>): R = visitor.visitCallExpr(this)
    }

    class Get(val instance: Expr, val name: Token) : Expr() {
        override fun <R> accept(visitor: Visitor<R>): R = visitor.visitGetExpr(this)
    }

    class Grouping(val expression: Expr) : Expr() {
        override fun <R> accept(visitor: Visitor<R>): R = visitor.visitGroupingExpr(this)
    }

    class Literal(val value: Any?) : Expr() {
        override fun <R> accept(visitor: Visitor<R>): R = visitor.visitLiteralExpr(this)
    }

    class Logical(val left: Expr, val operator: Token, val right: Expr) : Expr() {
        override fun <R> accept(visitor: Visitor<R>): R = visitor.visitLogicalExpr(this)
    }

    class Set(val instance: Expr, val name: Token, val value: Expr) : Expr() {
        override fun <R> accept(visitor: Visitor<R>): R = visitor.visitSetExpr(this)
    }

    class Super(val keyword: Token, val method: Token) : Expr() {
        override fun <R> accept(visitor: Visitor<R>): R = visitor.visitSuperExpr(this)
    }

    class This(val keyword: Token) : Expr() {
        override fun <R> accept(visitor: Visitor<R>): R = visitor.visitThisExpr(this)
    }

    class Unary(val operator: Token, val right: Expr) : Expr() {
        override fun <R> accept(visitor: Visitor<R>): R = visitor.visitUnaryExpr(this)
    }

    class Variable(val name: Token) : Expr() {
        override fun <R> accept(visitor: Visitor<R>): R = visitor.visitVariableExpr(this)
    }

    class Array(val elements: List<Expr>) :
        Expr() {
        override fun <R> accept(visitor: Visitor<R>): R = visitor.visitArrayExpr(this)
    }

    class GetSubscript(val array: Expr, val bracket: Token, val subscript: Expr) : Expr() {
        override fun <R> accept(visitor: Visitor<R>): R = visitor.visitGetSubscriptExpr(this)
    }

    class SetSubscript(val array: Expr, val bracket: Token, val subscript: Expr, val value: Expr) : Expr() {
        override fun <R> accept(visitor: Visitor<R>): R = visitor.visitSetSubscriptExpr(this)
    }

}
