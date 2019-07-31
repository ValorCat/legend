package legend.compiletime.expression.operation;

import legend.compiletime.expression.Expression;
import legend.compiletime.expression.value.LBoolean;
import legend.compiletime.expression.value.Value;
import legend.runtime.Scope;

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
        Value type = right.evaluate(scope);
        if (!type.isType("type")) {
            throw new RuntimeException("Cannot apply operator 'is' to types '" + value.type().getName() + "' and '"
                    + type.type().getName() + "'");
        }
        return LBoolean.resolve(value.type() == type && !negate);
    }

}
