package compiletime;

import compiletime.error.ErrorLog;
import compiletime.expression.Expression;
import compiletime.expression.Variable;
import compiletime.expression.value.LBoolean;
import compiletime.expression.value.LInteger;
import compiletime.expression.value.LNull;
import compiletime.expression.value.LString;

/**
 * A token is a fundamental symbol in the source code, such as an operator,
 * variable, or literal value. A token is represented by a type, value, and
 * sometimes a token sublist (in the case of grouping tokens like parentheses).
 * Tokens are created by the {@link Lexer} class and consumed by the {@link Parser}
 * class, which uses them to build a syntax tree.
 * @see Lexer
 * @see Parser
 * @since 12/21/2018
 */
public final class Token {

    public enum TokenType { EXPRESSION, GROUP, IDENTIFIER, INVALID, LITERAL, OPERATOR, PRINT_STRING }

    public final TokenType TYPE;
    public final String VALUE;
    public final Expression EXPRESSION;
    public final TokenLine CHILDREN;

    /* Use the static factory methods to create new tokens */
    private Token(TokenType type, String value, Expression expression, TokenLine tokens) {
        TYPE = type;
        VALUE = value;
        EXPRESSION = expression;
        CHILDREN = tokens;
    }

    /**
     * Check if this token is not an operator.
     * @return whether this token is not an operator
     */
    public boolean isValue() {
        return TYPE != TokenType.OPERATOR;
    }

    /**
     * Check if this token has a particular value. This is a
     * convenience method to replace long calls with equals().
     * @param value the value to check against
     * @return whether this token's value is the specified value
     */
    public boolean matches(String value) {
        return VALUE.equals(value);
    }

    /**
     * Convert this token into an expression object.
     * @return an expression object
     */
    public Expression asExpression() {
        switch (TYPE) {
            case EXPRESSION:
                return EXPRESSION;
            case IDENTIFIER:
                return new Variable(VALUE);
            case LITERAL:
                if (VALUE.matches("\\d+")) {
                    return new LInteger(Integer.parseInt(VALUE));
                } else if (VALUE.equals("true") || VALUE.equals("false")) {
                    return LBoolean.resolve(VALUE.equals("true"));
                } else if (VALUE.equals("null")) {
                    return LNull.NULL;
                } else {
                    return new LString(VALUE);
                }
            default:
                throw ErrorLog.get("Unexpected symbol '%s'", VALUE);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Token)) return false;
        Token token = (Token) o;
        return TYPE == token.TYPE && VALUE.equals(token.VALUE);
    }

    @Override
    public String toString() {
        String type = TYPE.name().substring(0, 3);
        if (CHILDREN.isEmpty()) {
            return String.format("%s \"%s\"", type, VALUE);
        } else if (VALUE == null) {
            return String.format("%s %s", type, CHILDREN);
        } else {
            return String.format("%s \"%s\" %s", type, VALUE, CHILDREN);
        }
    }

    public static Token newExpression(String value, Expression expr) {
        return new Token(TokenType.EXPRESSION, value, expr, TokenLine.EMPTY);
    }

    public static Token newGroup(String wrappers, TokenLine children) {
        return new Token(TokenType.GROUP, wrappers, null, children.copy());
    }

    public static Token newOperator(String value) {
        return new Token(TokenType.OPERATOR, value, null, TokenLine.EMPTY);
    }

    public static Token newToken(TokenType type, String value) {
        return new Token(type, value, null, TokenLine.EMPTY);
    }

}
