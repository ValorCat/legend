package expression.operation;

import expression.value.LString;
import expression.value.Value;
import execute.Environment;
import parse.Token;

import java.util.List;

/**
 * @since 2/22/2019
 */
public class ConcatenationOperation extends Operation {

    public ConcatenationOperation(int position, List<Token> tokens) {
        super(position, tokens);
    }

    @Override
    public Value evaluate(Environment env) {
        Value left = operands.get(0).evaluate(env);
        Value right = operands.get(1).evaluate(env);
        return new LString(left.asString() + right.asString());
    }

}
