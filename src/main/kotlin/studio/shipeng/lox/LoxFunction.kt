package studio.shipeng.lox


internal class LoxFunction(
    private val declaration: Stmt.Function,
    private val closure: Environment,
    private val isInitializer: Boolean
) : LoxCallable {
    override fun arity(): Int {
        return declaration.params.size
    }

    override fun call(interpreter: Interpreter, arguments: List<Any?>): Any {
        TODO("Not yet implemented")
    }

    fun bind(instance: LoxInstance): LoxFunction {
        val environment = Environment(closure)
        environment.define("this", instance)
        return LoxFunction(
            declaration, environment,
            isInitializer
        )
    }


    override fun toString(): String {
        return "<fn " + declaration.name.lexeme + ">"
    }
}