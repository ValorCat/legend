package legend.compiletime.expression.operation;

import legend.compiletime.TokenLine;
import legend.compiletime.expression.Expression;
import legend.compiletime.expression.value.Attribute;
import legend.compiletime.expression.value.Value;
import legend.runtime.Scope;

/**
 * @since 2/9/2019
 */
public class MemberSelectOperation extends BinaryOperation {

    private static final String OPERATOR = ".";

    public MemberSelectOperation(Expression target, Expression member) {
        super(OPERATOR, target, member);
    }

    @Override
    public void parse(TokenLine line, int operIndex) {
        super.parse(line, operIndex);
        right.getIdentifier(); // check that right operand is an identifier
    }

    @Override
    public Value evaluate(Scope scope) {
        Value target = left.evaluate(scope);
        return new Attribute(target, target.getAttribute(right.getIdentifier()));
    }

}
