package tokenize;

import java.util.List;

/**
 * @since 12/21/2018
 */
public class Token {

    public enum TokenType { IDENTIFIER, LITERAL, OPERATOR, OPERATION, PARENS }

    public final TokenType TYPE;
    public final String VALUE;
    public List<Token> CHILDREN;

    public Token(TokenType type, String value, List<Token> tokens) {
        TYPE = type;
        VALUE = value;
        CHILDREN = tokens;
    }

    public Token(TokenType type, String value, Token... tokens) {
        this(type, value, List.of(tokens));
    }

    public Token(TokenType type, String value) {
        this(type, value, List.of());
    }

    public Token(TokenType type, List<Token> tokens) {
        this(type, null, tokens);
    }

    public boolean isValue() {
        return TYPE == TokenType.IDENTIFIER
                || TYPE == TokenType.LITERAL
                || TYPE == TokenType.OPERATION;
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

    public static void consolidate(List<Token> list, Token result, int start, int length) {
        for (int i = 0; i < length; i++) {
            list.remove(start);
        }
        list.add(start, result);
    }

}
