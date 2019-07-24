package legend.compiletime.expression.operation;

import legend.compiletime.expression.Expression;
import legend.compiletime.expression.value.Value;
import legend.runtime.Scope;

public class FlippedBinaryOperation extends BinaryOperation {

    public FlippedBinaryOperation(String operator, Expression left, Expression right) {
        super(operator, left, right);
    }

    @Override
    public Value evaluate(Scope scope) {
        Value leftValue = left.evaluate(scope);
        Value rightValue = right.evaluate(scope);
        return rightValue.operateBinary(operator, leftValue);
    }

}
