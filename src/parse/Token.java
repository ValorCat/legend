package parse;

import dataformat.Expression;
import dataformat.Variable;
import dataformat.value.LBoolean;
import dataformat.value.LInteger;
import dataformat.value.LNull;
import dataformat.value.LString;

import java.util.Collections;
import java.util.List;

/**
 * A token is a fundamental symbol in the source code, such as an operator,
 * variable, or literal value. A token is represented by a type, value, and
 * sometimes a token sublist (in the case of grouping tokens like parentheses).
 * Tokens are created by the {@link Tokenizer} class and consumed by the {@link Parser}
 * class, which uses them to build a syntax tree.
 * @see Tokenizer
 * @see Parser
 * @since 12/21/2018
 */
public class Token {

    public enum TokenType { EXPRESSION, IDENTIFIER, LITERAL, OPERATOR, PARENS, STATEMENT }

    public final TokenType TYPE;
    public final String VALUE;
    public final Expression EXPRESSION;
    public final List<Token> CHILDREN;

    private Token(TokenType type, String value, Expression expression, List<Token> tokens) {
        TYPE = type;
        VALUE = value;
        EXPRESSION = expression;
        CHILDREN = tokens;
    }

    public Token(TokenType type, String value, Token... tokens) {
        this(type, value, null, List.of(tokens));
    }

    public Token(TokenType type, List<Token> tokens) {
        this(type, null, null, tokens);
    }

    public Token(String value, Expression expression) {
        this(TokenType.STATEMENT, value, expression, Collections.emptyList());
    }

    public Token(TokenType type, String value, Expression expression) {
        this(type, value, expression, Collections.emptyList());
    }

    /**
     * Check if this token is not an operator.
     * @return whether this token is not an operator
     */
    public boolean isValue() {
        return TYPE != TokenType.OPERATOR
                && TYPE != TokenType.STATEMENT;
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
            case EXPRESSION: case STATEMENT:
                return EXPRESSION;
            case IDENTIFIER:
                return new Variable(VALUE);
            case LITERAL:
                if (Character.isDigit(VALUE.charAt(0))) {
                    return new LInteger(Integer.parseInt(VALUE));
                } else if (VALUE.equals("true") || VALUE.equals("false")) {
                    return LBoolean.resolve(VALUE.equals("true"));
                } else if (VALUE.equals("null")) {
                    return LNull.NULL;
                } else {
                    return new LString(VALUE);
                }
            default:
                throw new RuntimeException("Unexpected token: " + VALUE);
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
        String type = TYPE.name().substring(0, 3) + TYPE.name().substring(TYPE.name().length() - 1);
        if (CHILDREN.isEmpty()) {
            return String.format("%s \"%s\"", type, VALUE);
        } else if (VALUE == null) {
            return String.format("%s %s", type, CHILDREN);
        } else {
            return String.format("%s \"%s\" %s", type, VALUE, CHILDREN);
        }
    }

    /**
     * Replace all the tokens between two indices in a list with a single token.
     * @param list the list to modify
     * @param result the replacement token
     * @param start the first index to remove
     * @param length the number of elements to remove after {@code start}
     */
    public static void consolidate(List<Token> list, Token result, int start, int length) {
        for (int i = 0; i < length; i++) {
            list.remove(start);
        }
        list.add(start, result);
    }

}
