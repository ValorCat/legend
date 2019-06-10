package compiletime.expression.operation;

import compiletime.Token;
import compiletime.expression.value.LBoolean;
import compiletime.expression.value.Value;
import runtime.Scope;

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
        if (left.type() == right.type()) {
            // operands are same type, so we can do (left==right) XOR (negate)
            return LBoolean.resolve(left.equals(right) != NEGATE);
        }
        List<String> types = List.of(left.type().getName(), right.type().getName());
        if (types.contains("str") && (types.contains("bool") || types.contains("int"))) {
            return LBoolean.resolve(NEGATE != left.asString().equals(right.asString()));
        }
        return LBoolean.FALSE;
    }

}
