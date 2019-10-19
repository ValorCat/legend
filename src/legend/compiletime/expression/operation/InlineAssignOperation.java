package legend.compiletime.expression.operation;

import legend.compiletime.TokenLine;
import legend.compiletime.expression.Expression;
import legend.compiletime.expression.value.Value;
import legend.runtime.Scope;
import legend.runtime.type.NoType;

/**
 * @since 2/9/2019
 */
public class InlineAssignOperation extends BinaryOperation {

    private static final String OPERATOR = ":=";

    public InlineAssignOperation(Expression target, Expression value) {
        super(OPERATOR, target, value);
    }

    @Override
    public void parse(TokenLine line, int operIndex) {
        super.parse(line, operIndex);
        left.getIdentifier(); // check that the left operand is an identifier
    }

    @Override
    public Value evaluate(Scope scope) {
        Value value = right.evaluate(scope);
        scope.setVariable(left.getIdentifier(), NoType.NO_TYPE, value);
        return value;
    }

}
