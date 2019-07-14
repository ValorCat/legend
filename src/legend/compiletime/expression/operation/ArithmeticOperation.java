package legend.compiletime.expression.operation;

import legend.compiletime.Token;
import legend.compiletime.expression.value.Value;
import legend.runtime.Scope;

import java.util.List;

/**
 * @since 1/19/2019
 */
public class ArithmeticOperation extends Operation {

    public ArithmeticOperation(int position, List<Token> tokens) {
        super(position, tokens);
    }

    @Override
    public Value evaluate(Scope scope) {
        Value left = operands.get(0).evaluate(scope);
        Value right = operands.get(0).evaluate(scope);
        return left.operateBinary(operator, right);
    }

}
