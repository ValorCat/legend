package parse;

import javafx.util.Pair;
import tokenize.Token;

import java.util.*;
import java.util.function.BiConsumer;

/**
 * @since 12/22/2018
 */
public final class OperatorTable {

    @SuppressWarnings("unchecked")
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
                    new Pair<>("match", OperationsParsing::matchExpression),
                    new Pair<>("repeat", OperationsParsing::handleStandaloneKeyword))
    );

    public static final Set<String> LONG_SYMBOLS = Set.of("==", "!=", "<=", ">=");

    public interface OperationHandler extends BiConsumer<Integer, List<Token>> {}

    private Map<String, Pair<Integer, OperationHandler>> implTable;

    private OperatorTable(int initialCapacity) {
        implTable = new HashMap<>(initialCapacity);
    }

    private int getPrecedence(String operator) {
        Pair<Integer, OperationHandler> pair = implTable.get(operator);
        if (pair == null) {
            throw new RuntimeException("Invalid operator '" + operator + "'");
        }
        return pair.getKey();
    }

    public OperationHandler getHandler(String operator) {
        Pair<Integer, OperationHandler> pair = implTable.get(operator);
        if (pair == null) {
            throw new RuntimeException("Invalid operator '" + operator + "'");
        }
        return pair.getValue();
    }

    public static Comparator<Token> byPrecedence() {
        return Comparator.comparing(token -> OPERATORS.getPrecedence(token.VALUE));
    }

    @SafeVarargs
    private static OperatorTable defineOperations(
            List<Pair<String, OperationHandler>>... operators) {
        OperatorTable temp = new OperatorTable(operators.length);
        for (int i = 0; i < operators.length; i++) {
            for (Pair<String, OperationHandler> pair : operators[i]) {
                temp.implTable.put(pair.getKey(), new Pair<>(i, pair.getValue()));
            }
        }
        return temp;
    }

}
