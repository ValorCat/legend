package parse;

import dataformat.operation.*;
import dataformat.operation.flow.*;
import dataformat.operation.function.BinaryOperatorCall;
import dataformat.operation.function.FunctionCall;
import dataformat.operation.function.UnaryOperatorCall;

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
            {".", "call"},
            {"unop", "biop"},       // high precedence
            {"^"},
            {"*", "/"},
            {"+", "-"},
            {"<", "<=", ">", ">="},
            {"==", "!=", ":="},
            {":"},
            {","},
            {"="},
            {"in"},                 // low precedence
            {"else", "elsif", "end", "for", "if", "repeat", "while"}
    });

    /*
    These two constants are used by the tokenizer to assign the correct type
    to unusual operators.
     */
    public static final Set<String> LONG_SYMBOLS = Set.of("==", "!=", "<=", ">=", ":=");
    public static final Set<String> KEYWORDS = Set.of("else", "elsif", "end", "for", "if", "repeat", "while");

    /**
     * Verify an operator's environmental constraints are met (e.g. for the '+'
     * operator, there must be a value on either side) and then build a partial
     * syntax tree around the operator. If they are met, the operand tokens are
     * consolidated into this operation's newly created token. Flow control
     * structures are also pushed on and popped off the stack as they are
     * encountered.
     * @param tokenPos the index of the operator in the list
     * @param statement the list of tokens
     * @param address the address of this statement
     * @param controlStack the stack of flow control structures
     */
    public static void parseOperation(int tokenPos, List<Token> statement, int address,
                                      Stack<FlowController> controlStack) {
        switch (statement.get(tokenPos).VALUE) {
            case ".":       new DotOperation(tokenPos, statement); break;
            case "call":    new FunctionCall(tokenPos, statement); break;
            case "unop":    new UnaryOperatorCall(tokenPos, statement); break;
            case "biop":    new BinaryOperatorCall(tokenPos, statement); break;
            case ":=":      new AssignmentExpression(tokenPos, statement); break;
            case ":":       new Mapping(tokenPos, statement); break;
            case ",":       new CommaList(tokenPos, statement); break;
            case "=":       new Assignment(tokenPos, statement); break;
            case "end":     new EndStatement(tokenPos, statement, address, controlStack); break;
            case "if":      new IfStatement(tokenPos, statement, address, controlStack); break;
            case "for":     new ForStatement(tokenPos, statement, controlStack); break;
            case "repeat":  new RepeatStatement(tokenPos, statement, controlStack); break;
            case "while":   new WhileStatement(tokenPos, statement, controlStack); break;
            case "^": case "*": case "/": case "+": case "-":
                            new ArithmeticOperation(tokenPos, statement); break;
            case "<": case ">": case "<=": case ">=":
                            new ComparisonOperation(tokenPos, statement); break;
            case "==": case "!=":
                            new EqualsOperation(tokenPos, statement); break;
            case "else": case "elsif":
                            if (controlStack.isEmpty()) {
                                throw new RuntimeException("Unexpected symbol '" + statement.get(tokenPos).VALUE + "'");
                            }
                            controlStack.peek().setJumpPoint(address, tokenPos, statement); break;
            case "in":      break; // ignore
            default: throw new RuntimeException("Invalid operator '" + statement.get(tokenPos).VALUE + "'");
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
