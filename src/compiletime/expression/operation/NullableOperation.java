package compiletime.expression.operation;

import compiletime.Token;
import compiletime.TokenLine;
import compiletime.expression.value.Value;
import compiletime.expression.value.type.NullableType;
import compiletime.expression.value.type.Type;
import runtime.Scope;

import java.util.List;

/**
 * @since 2/26/2019
 */
public class NullableOperation extends Operation {

    public NullableOperation(int position, List<Token> tokens) {
        super(position, tokens);
    }

    @Override
    protected void parse(int pos, TokenLine tokens) {
        parseRightUnaryOperation(pos, tokens);
    }

    @Override
    public Value evaluate(Scope scope) {
        Value type = operands.get(0).evaluate(scope);
        if (!type.isType("Type")) {
            throw new RuntimeException("Cannot apply '?' operator to object of type '" + type.type().getName() + "'");
        }
        return new NullableType(((Type) type));
    }

}
