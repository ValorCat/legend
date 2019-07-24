package legend.compiletime.expression.operation;

import legend.compiletime.expression.Expression;
import legend.compiletime.expression.group.SquareBrackets;
import legend.compiletime.expression.value.Value;
import legend.runtime.Scope;

public class SubscriptOperation extends BinaryOperation {

    private static final String OPERATOR = "[]";

    public SubscriptOperation(Expression subscriptable, SquareBrackets subscript) {
        super(OPERATOR, subscriptable, subscript);
    }

    @Override
    public Value evaluate(Scope scope) {
        Value subscriptable = left.evaluate(scope);
        Value subscript = ((SquareBrackets) right).getContents().evaluate(scope);
        return subscriptable.operateBinary(operator, subscript);
    }

}
