package legend.compiletime.expression.operation;

import legend.compiletime.expression.Expression;
import legend.compiletime.expression.value.BoolValue;
import legend.compiletime.expression.value.Value;
import legend.runtime.Scope;
import legend.runtime.type.Type;

/**
 * @since 2/9/2019
 */
public class IsOperation extends BinaryOperation {

    private static final String OPERATOR = "is";

    private boolean negate;

    public IsOperation(Expression value, Expression type, boolean negate) {
        super(OPERATOR, value, type);
        this.negate = negate;
    }

    @Override
    public Value evaluate(Scope scope) {
        Value value = left.evaluate(scope);
        Type type = right.evaluate(scope).asType();
        return BoolValue.resolve(type.isSupertypeOf(value.type()) != negate);
    }

}
