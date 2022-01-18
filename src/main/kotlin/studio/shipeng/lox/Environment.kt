package studio.shipeng.lox


class Environment(val enclosing: Environment?) {
    private val values = hashMapOf<String, Any?>()
    constructor() : this(null)  {}

    fun get(name: Token): Any? {
        if (values.containsKey(name.lexeme)) {
            return values[name.lexeme]
        }
        if (enclosing != null) return enclosing.get(name)
        throw RuntimeError(
            name,
            "Undefined variable '" + name.lexeme + "'."
        )
    }

    fun assign(name: Token, value: Any?) {
        if (values.containsKey(name.lexeme)) {
            values[name.lexeme] = value
            return
        }

        if (enclosing != null) {
            enclosing.assign(name, value)
            return
        }
        throw RuntimeError(
            name,
            "Undefined variable '" + name.lexeme + "'."
        )
    }

    fun define(name: String, value: Any?) {
        values[name] = value
    }

    fun ancestor(distance: Int): Environment {
        var environment: Environment = this
        for (i in 0 until distance) {
            environment = environment.enclosing!!
        }
        return environment
    }

    fun getAt(distance: Int, name: String): Any? {
        return ancestor(distance).values[name]
    }

    fun assignAt(distance: Int, name: Token, value: Any?) {
        ancestor(distance).values[name.lexeme] = value
    }

    override fun toString(): String {
        var result = values.toString()
        if (enclosing != null) {
            result += " -> $enclosing"
        }
        return result
    }

}