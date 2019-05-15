package expression.operation;

import execute.Scope;
import expression.value.LString;
import expression.value.Value;
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
    public Value evaluate(Scope scope) {
        Value left = operands.get(0).evaluate(scope);
        Value right = operands.get(1).evaluate(scope);
        return new LString(left.asString() + right.asString());
    }

}
