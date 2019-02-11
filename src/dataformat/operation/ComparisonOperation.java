package dataformat.operation;

import dataformat.value.BoolValue;
import dataformat.value.Value;
import execute.Environment;
import parse.Token;

import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;

/**
 * @since 1/19/2019
 */
public class ComparisonOperation extends Operation {

    private static final Map<String, BiPredicate<Integer, Integer>> OPERATIONS = Map.of(
            "<", (a, b) -> a < b,
            "<=", (a, b) -> a <= b,
            ">", (a, b) -> a > b,
            ">=", (a, b) -> a >= b
    );

    public ComparisonOperation(int position, List<Token> tokens) {
        super(position, tokens);
    }

    @Override
    public Value evaluate(Environment env) {
        int left = operands.get(0).evaluate(env).asInteger();
        int right = operands.get(1).evaluate(env).asInteger();
        boolean result = OPERATIONS.get(operator).test(left, right);
        return BoolValue.resolve(result);
    }
}
