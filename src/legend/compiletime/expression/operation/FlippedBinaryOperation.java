package legend.compiletime.expression.operation;

import legend.compiletime.expression.Expression;
import legend.compiletime.expression.value.Value;
import legend.runtime.Scope;

/**
 * Most binary operations use the datatype of the left operand to determine which operation handler to call. This class
 * represents operations for whom it is more appropriate to use the right operand.
 *
 * For example, in the expression
 *
 *      10 in List(5, 10, 15)
 *
 * the implementation for 'in' should be defined by the List type, not the int type.
 */
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
