package legend.compiletime;

import legend.compiletime.error.ErrorLog;
import legend.compiletime.error.InterpreterException;
import legend.compiletime.expression.operation.*;

import java.util.*;

/**
 * This class stores valid operators and their precedence levels. It also instantiates
 * the expression objects that form syntax trees during parsing.
 * @since 12/22/2018
 */
public final class OperatorTable {

    /*
    Below is the list of operators defined in Legend, ordered by descending precedence
    level. Some operators, like "[]", don't correspond to a particular token, but
    instead an arrangement of other tokens. For example, a function call is indicated
    by an identifier followed by parentheses. These special operations are detected
    during tokenization and their corresponding operators are inserted implicitly.
     */
    private static final OperatorTable OPERATORS = defineOperations(new String[][] {
            {".", "()", "[]"},      // high precedence
            {"#", "?"},
            {"not"},
            {"^"},
            {"*", "/"},
            {"+", "-"},
            {"mod"},
            {"==", "!=", "<", "<=", ">", ">="},
            {"&"},
            {"and", "or", "nor"},
            {":="},
            {","},
            {"in"}                  // low precedence
    });

    /*
    These two constants are used by the lexer to assign the correct type
    to unusual operators.
     */
    public static final Set<String> LONG_SYMBOLS = Set.of("==", "!=", "<=", ">=", ":=");
    public static final Set<String> KEYWORDS = Set.of(
            "and", "def", "else", "end", "for", "if", "mod", "or", "nor", "not", "repeat", "return", "while"
    );

    private static final Set<String> LEFT_UNARY = Set.of("#", "not");
    private static final Set<String> RIGHT_UNARY = Set.of("?");
    private static final Set<String> SPECIAL = Set.of(".", "()", ":=", ",");

    /**
     * Verify an operator's environmental constraints are met (e.g. for the '+'
     * operator, there must be a value on either side) and then build a partial
     * syntax tree around the operator. If they are met, the operand tokens are
     * consolidated into this operation's newly created token. Flow control
     * structures are also pushed on and popped off the stack as they are
     * encountered.
     * @param operIndex the index of the operator in the list
     * @param tokens the list of tokens
     */
    public static void parseOperation(int operIndex, List<Token> tokens) {
        String operator = tokens.get(operIndex).VALUE;
        TokenLine line = (TokenLine) tokens;

        if (LEFT_UNARY.contains(operator)) {
            UnaryOperation.parseLeft(operIndex, line);
        } else if (RIGHT_UNARY.contains(operator)) {
            UnaryOperation.parseRight(operIndex, line);
        } else if (!SPECIAL.contains(operator)) {
            BinaryOperation.parse(operIndex, line);
        } else {
            switch (operator) {
                case ".":   MemberSelectOperation.parse(operIndex, line); break;
                case "()":  InvokeOperation.parse(operIndex, line); break;
                case ":=":  InlineAssignOperation.parse(operIndex, line); break;
                case ",":   CommaOperation.parse(operIndex, line); break;
                case "in":  break;
                default:    throw getException(operator);
            }
        }
//
//        switch (operator.VALUE) {
//            case ".":       new DotOperation(operIndex, tokens); break;
//            case "()":      new InvokeOperation(operIndex, tokens); break;
//            case "[]":      new IndexOperation(operIndex, tokens); break;
//            case "#":       new LengthOperation(operIndex, tokens); break;
//            case "?":       new NullableOperation(operIndex, tokens); break;
//            case "not":     new NotOperation(operIndex, tokens); break;
//            case "^": case "*": case "/": case "mod": case "+": case "-":
//                            new ArithmeticOperation(operIndex, tokens); break;
//            case "==": case "!=":
//                            new EqualsOperation(operIndex, tokens); break;
//            case "<": case ">": case "<=": case ">=":
//                            new ComparisonOperation(operIndex, tokens); break;
//            case "&":       new ConcatenationOperation(operIndex, tokens); break;
//            case "and": case "or": case "nor":
//                            new LogicalOperation(operIndex, tokens); break;
//            case ":=":      new AssignmentExpression(operIndex, tokens); break;
//            case ",":       new CommaList(operIndex, tokens); break;
//            case "in":      break; // ignore
//            default:        throw getException(operator.VALUE);
//        }
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
            throw getException(operator);
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

    private static InterpreterException getException(String operator) {
        if (operator.equals("=")) {
            return ErrorLog.get("Cannot use '=' in an expression (did you mean ':='?)");
        }
        return ErrorLog.get("Unrecognized operator '%s'", operator);
    }

}
