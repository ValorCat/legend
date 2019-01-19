package parse;

import dataformat.operation.*;

import java.util.*;

/**
 * This class stores valid operators and their precedence levels. It also instantiates
 * the expression objects that form syntax trees during parsing.
 * @since 12/22/2018
 */
public final class OperatorTable {

    /*
    Below is the list of operators defined in Legend, ordered by descending precedence
    level. Some operators, like "call", don't correspond to a particular token, but
    instead an arrangement of other tokens. For example, a function call is indicated
    by an identifier followed by parentheses. These special operations are detected
    during tokenization and their corresponding operators are inserted implicitly.
     */
    private static final OperatorTable OPERATORS = defineOperations(new String[][] {
            {".", "call"},          // highest precedence
            {"^"},
            {"*", "/"},
            {"+", "-"},
            {"to"},
            {"<", "<=", ">", ">="},
            {"==", "!="},
            {":"},
            {","},
            {"="},
            {"end", "repeat"}       // lowest precedence
    });

    /*
    These two constants are used by the tokenizer to assign the correct type
    to unusual operators.
     */
    public static final Set<String> LONG_SYMBOLS = Set.of("==", "!=", "<=", ">=");
    public static final Set<String> KEYWORDS = Set.of("else", "end", "match", "repeat", "to");

    /**
     * Verify an operator's environmental constraints are met (e.g. for the '+'
     * operator, there must be a value on either side) and then build a partial
     * syntax tree around the operator.
     * @param pos the index of the operator in the list
     * @param statement the list of tokens
     */
    public static void parseOperation(int pos, List<Token> statement) {
        switch (statement.get(pos).VALUE) {
            case ".":
                new DotOperation(pos, statement);
                break;
            case "call":
                new FunctionCall(pos, statement);
                break;
            case "^": case "*": case "/": case "+": case "-":
                new ArithmeticOperation(pos, statement);
                break;
            case "to":
                new ToOperation(pos, statement);
                break;
            case "<": case ">": case "<=": case ">=":
                new ComparisonOperation(pos, statement);
                break;
            case "==": case "!=":
                new EqualsOperation(pos, statement);
                break;
            case ":":
                new Mapping(pos, statement);
                break;
            case ",":
                new CommaList(pos, statement);
                break;
            case "=":
                new Assignment(pos, statement);
                break;
            case "end":
                new EndStatement(pos, statement);
                break;
            case "repeat":
                new RepeatStatement(pos, statement);
                break;
            default: throw new RuntimeException("Invalid operator '" + statement.get(pos).VALUE + "'");
        }
    }

    private Map<String, Integer> implTable;

    private OperatorTable(int initialCapacity) {
        implTable = new HashMap<>(initialCapacity);
    }

    /**
     * Build and return  a token comparator that orders elements by their
     * operator precedence level from high to low.
     * @return a comparator that orders tokens by descending precedence level
     */
    public static Comparator<Token> byPrecedence() {
        return Comparator.comparing(token -> OPERATORS.getPrecedence(token.VALUE));
    }

    /**
     * Determine the precedence level of an operator.
     * @param operator the operator to look up
     * @return the operator's precedence level, where 0 is the highest
     */
    private int getPrecedence(String operator) {
        Integer precedence = implTable.get(operator);
        if (precedence == null) {
            throw new RuntimeException("Invalid operator '" + operator + "'");
        }
        return precedence;
    }

    /**
     * An internal utility method to facilitate adding new operators
     * to the operation table.
     * @param operators the operators to add
     * @return the complete operator table
     */
    private static OperatorTable defineOperations(String[][] operators) {
        OperatorTable temp = new OperatorTable(operators.length);
        for (int i = 0; i < operators.length; i++) {
            for (String symbol : operators[i]) {
                temp.implTable.put(symbol, i);
            }
        }
        return temp;
    }

}
