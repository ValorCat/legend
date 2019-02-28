package expression.operation;

import expression.value.LBoolean;
import expression.value.Value;
import execute.Environment;
import parse.Token;

import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;

/**
 * @since 1/19/2019
 */
public class LogicalOperation extends Operation {

    private static final Map<String, BiPredicate<Boolean, Boolean>> OPERATIONS = Map.of(
            "and", (a, b) -> a && b,
            "or", (a, b) -> a || b,
            "nor", (a, b) -> !(a || b)
    );

    public LogicalOperation(int position, List<Token> tokens) {
        super(position, tokens);
    }

    @Override
    public Value evaluate(Environment env) {
        boolean left = operands.get(0).evaluate(env).asBoolean();
        boolean right = operands.get(1).evaluate(env).asBoolean();
        boolean result = OPERATIONS.get(operator).test(left, right);
        return LBoolean.resolve(result);
    }
}
