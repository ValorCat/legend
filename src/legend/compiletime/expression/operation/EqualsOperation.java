package legend.compiletime.expression.operation;

import legend.compiletime.Token;
import legend.compiletime.expression.value.LBoolean;
import legend.compiletime.expression.value.Value;
import legend.runtime.Scope;

import java.util.List;

/**
 * @since 1/19/2019
 */
public class EqualsOperation extends Operation {

    private final boolean NEGATE;

    public EqualsOperation(int position, List<Token> tokens) {
        super(position, tokens);
        NEGATE = operator.equals("!=");
    }

    @Override
    public Value evaluate(Scope scope) {
        Value left = operands.get(0).evaluate(scope);
        Value right = operands.get(1).evaluate(scope);
        return LBoolean.resolve(left.type() == right.type() && left.equals(right) != NEGATE);
    }

}
