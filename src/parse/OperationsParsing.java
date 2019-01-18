package parse;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static parse.Token.TokenType.*;

/**
 * Each static method in this utility class corresponds to one or more Legend
 * operations. During parsing, these methods check the environmental constraints
 * of an operator and throw an exception if a constraint is not met.
 * @since 12/22/2018
 */
public final class OperationsParsing {

    private OperationsParsing() {}

    public static void assignVariable(int pos, List<Token> tokens) {
        // todo handle expressions in assignment lhs (eg x.y = z)
        binaryOperation(pos, tokens,
                left -> left.TYPE == IDENTIFIER, "Expected variable name for assignment",
                Token::isValue, "Expected variable value for assignment");
    }

    public static void callFunction(int pos, List<Token> tokens) {
        binaryOperation(pos, tokens,
                Token::isValue, "Expected function object",
                Token::isValue, "Expected function arguments");
    }

    public static void getAttribute(int pos, List<Token> tokens) {
        boolean leftValue = pos > 0 && tokens.get(pos - 1).isValue();
        boolean rightValue = pos != tokens.size() - 1 && tokens.get(pos + 1).isValue();
        if (leftValue && !rightValue) {
            tokens.add(pos + 1, new Token(LITERAL, "0"));
        } else if (!leftValue && rightValue) {
            tokens.add(pos, new Token(LITERAL, "0"));
        }
        binaryOperation(pos, tokens);
    }

    public static void handleElse(int pos, List<Token> tokens) {
        if (pos > 0) {
            throw new RuntimeException("Unexpected symbol 'else'");
        } else if (pos == tokens.size() - 1) {
            throw new RuntimeException("Expected block after 'else'");
        } else if (tokens.get(pos + 1).TYPE != OPERATOR || !tokens.get(pos + 1).VALUE.equals(":")) {
            throw new RuntimeException("Expected ':' after 'else'");
        }
        tokens.set(pos, new Token(IDENTIFIER, "else"));
    }

    public static void handleStandaloneKeyword(int pos, List<Token> tokens) {
        String keyword = tokens.get(pos).VALUE;
        if (pos > 0) {
            throw new RuntimeException("Unexpected symbol '" + keyword + "'");
        } else if (tokens.size() > 1) {
            throw new RuntimeException("Unexpected symbol after '" + keyword + "'");
        }
        tokens.set(pos, new Token(OPERATION, tokens.get(pos).VALUE));
    }

    public static void mapKeyToValue(int pos, List<Token> tokens) {
        binaryOperation(pos, tokens);
    }

    public static void matchExpression(int pos, List<Token> tokens) {
        if (pos == tokens.size() - 1 || !tokens.get(pos + 1).isValue()) {
            throw new RuntimeException("Expected value after 'match'");
        }
        tokens.set(pos, new Token(OPERATION, "match", tokens.get(pos + 1)));
        tokens.remove(pos + 1);
    }

    public static void separateByCommas(int pos, List<Token> tokens) {
        binaryOperation(pos, tokens,
                Token::isValue, "Expected value before comma",
                Token::isValue, "Expected value after comma");
        List<Token> children = tokens.get(pos - 1).CHILDREN;
        if (children.get(0).VALUE.equals(",")) {
            List<Token> newChildren = new ArrayList<>(children.get(0).CHILDREN);
            newChildren.add(children.get(1));
            tokens.get(pos - 1).CHILDREN = newChildren;
        }
    }

    public static void binaryOperation(int pos, List<Token> tokens) {
        String operator = tokens.get(pos).VALUE;
        binaryOperation(pos, tokens,
                Token::isValue, "Expected left operand for '" + operator + "'",
                Token::isValue, "Expected right operand for '" + operator + "'");
    }

    private static void binaryOperation(int pos, List<Token> tokens, Predicate<Token> leftCheck, String leftError,
                                        Predicate<Token> rightCheck, String rightError) {
        Token left = null;
        Token right = null;
        if (pos > 0) left = tokens.get(pos - 1);
        if (pos < tokens.size() - 1) right = tokens.get(pos + 1);

        if (left == null || !leftCheck.test(left)) {
            throw new RuntimeException(leftError);
        } else if (right == null || !rightCheck.test(right)) {
            throw new RuntimeException(rightError);
        }

        Token operation = new Token(OPERATION, tokens.get(pos).VALUE, left, right);
        Token.consolidate(tokens, operation, pos - 1, 3);
    }

}
