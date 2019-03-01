package parse;

import expression.operation.*;

import java.util.*;

import static parse.ErrorDescription.UNKNOWN_OPER;

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
            {".", "call"},      // high precedence
            {"#", "?"},
            {"unop", "biop"},
            {"not"},
            {"^"},
            {"*", "/", "%"},
            {"+", "-"},
            {"==", "!=", "<", "<=", ">", ">="},
            {"and", "or", "nor"},
            {"::"},
            {":="},
            {":"},
            {","},
            {"in"}              // low precedence
    });

    /*
    These two constants are used by the lexer to assign the correct type
    to unusual operators.
     */
    public static final Set<String> LONG_SYMBOLS = Set.of("==", "!=", "<=", ">=", "::", ":=");
    public static final Set<String> KEYWORDS = Set.of(
            "and", "def", "else", "elsif", "end", "for", "if", "or", "nor", "not", "repeat", "return", "while"
    );

    /**
     * Verify an operator's environmental constraints are met (e.g. for the '+'
     * operator, there must be a value on either side) and then build a partial
     * syntax tree around the operator. If they are met, the operand tokens are
     * consolidated into this operation's newly created token. Flow control
     * structures are also pushed on and popped off the stack as they are
     * encountered.
     * @param tokenPos the index of the operator in the list
     * @param tokens the list of tokens
     */
    public static void parseOperation(int tokenPos, List<Token> tokens) {
        switch (tokens.get(tokenPos).VALUE) {
            case ".":       new DotOperation(tokenPos, tokens); break;
            case "call":    new FunctionCall(tokenPos, tokens); break;
            case "#":       new LengthOperation(tokenPos, tokens); break;
            case "?":       new NullableOperation(tokenPos, tokens); break;
            case "unop":    new UnaryOperatorCall(tokenPos, tokens); break;
            case "biop":    new BinaryOperatorCall(tokenPos, tokens); break;
            case "not":     new NotOperation(tokenPos, tokens); break;
            case "^": case "*": case "/": case "%": case "+": case "-":
                            new ArithmeticOperation(tokenPos, tokens); break;
            case "==": case "!=":
                            new EqualsOperation(tokenPos, tokens); break;
            case "<": case ">": case "<=": case ">=":
                            new ComparisonOperation(tokenPos, tokens); break;
            case "and": case "or": case "nor":
                            new LogicalOperation(tokenPos, tokens); break;
            case "::":      new ConcatenationOperation(tokenPos, tokens); break;
            case ":=":      new AssignmentExpression(tokenPos, tokens); break;
            case ":":       new Mapping(tokenPos, tokens); break;
            case ",":       new CommaList(tokenPos, tokens); break;
            case "in":      break; // ignore
            default:
                throw ParserError.error(UNKNOWN_OPER, "Unrecognized operator '%s'", tokens.get(tokenPos));
        }
    }

    /**
     * Operations are internally stored in this map, where the keys represent
     * operators and the values represent precedence levels.
     */
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
            throw ParserError.error(UNKNOWN_OPER, "Unrecognized operator '%s'", operator);
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
