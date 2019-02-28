package expression.operation;

import expression.value.NullableType;
import expression.value.Type;
import expression.value.Value;
import execute.Environment;
import parse.Token;

import java.util.List;

/**
 * @since 2/26/2019
 */
public class NullableOperation extends Operation {

    public NullableOperation(int position, List<Token> tokens) {
        super(position, tokens);
    }

    @Override
    protected void parse(int pos, List<Token> tokens) {
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
