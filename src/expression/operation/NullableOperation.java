package expression.operation;

import execute.Environment;
import expression.value.Value;
import expression.value.type.NullableType;
import expression.value.type.Type;
import parse.Token;
import parse.TokenLine;

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
    public Value evaluate(Environment env) {
        Value type = operands.get(0).evaluate(env);
        if (!type.isType("Type")) {
            throw new RuntimeException("Cannot apply '?' operator to object of type '" + type.type().getName() + "'");
        }
        return new NullableType(((Type) type));
    }

}
