package expression.operation;

import execute.Scope;
import expression.value.LInteger;
import expression.value.Value;
import parse.Token;

import java.util.List;
import java.util.Map;
import java.util.function.IntBinaryOperator;

/**
 * @since 1/19/2019
 */
public class ArithmeticOperation extends Operation {

    private static final Map<String, IntBinaryOperator> OPERATIONS = Map.of(
            "+", (a, b) -> a + b,
            "-", (a, b) -> a - b,
            "*", (a, b) -> a * b,
            "/", (a, b) -> Math.round((float) a / b),
            "%", (a, b) -> a % b,
            "^", (a, b) -> (int) Math.pow(a, b)
    );

    public ArithmeticOperation(int position, List<Token> tokens) {
        super(position, tokens);
    }

    @Override
    public Value evaluate(Scope scope) {
        int left = operands.get(0).evaluate(scope).asInteger();
        int right = operands.get(1).evaluate(scope).asInteger();
        int result = OPERATIONS.get(operator).applyAsInt(left, right);
        return new LInteger(result);
    }

}
