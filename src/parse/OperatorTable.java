package parse;

import javafx.util.Pair;

import java.util.*;
import java.util.function.BiConsumer;

/**
 * This class maintains a table of all defined operators, along with their precedence
 * level and environmental constraints. It is referenced during tokenization and
 * parsing. The executation of operations is handled by {@link execute.OperationsExecution}.
 * @see OperationsParsing
 * @see execute.OperationsExecution
 * @since 12/22/2018
 */
public final class OperatorTable {

    /*
    Below is the list of operators defined in Legend. Some operators, like "call",
    don't correspond to a particular token, but instead an arrangement of other
    tokens. For example, a function call is indicated by an identifier followed by
    parentheses. These special operations are detected during tokenization and their
    corresponding operators are inserted implicitly.

    Here are some important notes about the operator table:
        1.  Operators are ordered from high to low precedence. A row beginning with
            "List.of" marks a new precedence level, while a row without this marker
            is a continuation of the previous level.
        2.  The method reference to the right of each operator points to a method
            that ensures the operator has valid operands.
        3.  If an operator is not listed here, it will throw an exception when it
            is encountered by the parser.
     */
    public static final OperatorTable OPERATORS = defineOperations(
            List.of(new Pair<>(".",     OperationsParsing::getAttribute),
                    new Pair<>("call",  OperationsParsing::callFunction)),
            List.of(new Pair<>("^",     OperationsParsing::binaryOperation)),
            List.of(new Pair<>("*",     OperationsParsing::binaryOperation),
                    new Pair<>("/",     OperationsParsing::binaryOperation)),
            List.of(new Pair<>("+",     OperationsParsing::binaryOperation),
                    new Pair<>("-",     OperationsParsing::binaryOperation)),
            List.of(new Pair<>("to",    OperationsParsing::binaryOperation)),
            List.of(new Pair<>("<",     OperationsParsing::binaryOperation),
                    new Pair<>("<=",    OperationsParsing::binaryOperation),
                    new Pair<>(">",     OperationsParsing::binaryOperation),
                    new Pair<>(">=",    OperationsParsing::binaryOperation)),
            List.of(new Pair<>("==",    OperationsParsing::binaryOperation),
                    new Pair<>("!=",    OperationsParsing::binaryOperation)),
            List.of(new Pair<>("else",  OperationsParsing::handleElse)),
            List.of(new Pair<>(":",     OperationsParsing::mapKeyToValue)),
            List.of(new Pair<>(",",     OperationsParsing::separateByCommas)),
            List.of(new Pair<>("=",     OperationsParsing::assignVariable)),
            List.of(new Pair<>("end",   OperationsParsing::handleStandaloneKeyword),
                    new Pair<>("repeat", OperationsParsing::handleStandaloneKeyword))
    );

    /*
    These two constants are used by the tokenizer to assign the correct type
    to unusual operators.
     */
    public static final Set<String> LONG_SYMBOLS = Set.of("==", "!=", "<=", ">=");
    public static final List<String> KEYWORDS = List.of("else", "end", "match", "repeat", "to");

    /*
    An operation validator is a method that checks if the tokens at and around
    a particular index in a token list match certain constraints. If they do
    not, an exception is thrown. For example, the '+' operation is valid if
    and only if it is surrounded by parseable values and not other operators.
     */
    private interface OperationValidator extends BiConsumer<Integer, List<Token>> {}

    private Map<String, Pair<Integer, OperationValidator>> implTable;

    private OperatorTable(int initialCapacity) {
        implTable = new HashMap<>(initialCapacity);
    }

    /**
     * Check if an operator's environmental constraints are met. If they
     * aren't, an exception is thrown. For example, the '+' operator cannot
     * be adjacent to another operator.
     * @param position the index of the operator in the token list
     * @param statement the list of tokens
     */
    public void validate(int position, List<Token> statement) {
        String operator = statement.get(position).VALUE;
        Pair<Integer, OperationValidator> pair = implTable.get(operator);
        if (pair == null) {
            throw new RuntimeException("Invalid operator '" + operator + "'");
        }
        pair.getValue().accept(position, statement);
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
        Pair<Integer, OperationValidator> pair = implTable.get(operator);
        if (pair == null) {
            throw new RuntimeException("Invalid operator '" + operator + "'");
        }
        return pair.getKey();
    }

    /**
     * An internal utility method to facilitate adding new operators
     * to the operation table.
     * @param operators the operators to add
     * @return the complete operator table
     */
    @SafeVarargs
    private static OperatorTable defineOperations(
            List<Pair<String, OperationValidator>>... operators) {
        OperatorTable temp = new OperatorTable(operators.length);
        for (int i = 0; i < operators.length; i++) {
            for (Pair<String, OperationValidator> pair : operators[i]) {
                temp.implTable.put(pair.getKey(), new Pair<>(i, pair.getValue()));
            }
        }
        return temp;
    }

}
