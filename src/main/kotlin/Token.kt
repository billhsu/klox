class Token(val type: TokenType, val lexeme: String, val literal: Any?, val line: Int) {
    override fun toString(): String {
        if (literal != null) return "Token(type=$type, lexeme='$lexeme')"
        return "Token(type=$type, lexeme='$lexeme', literal=$literal)"
    }
}