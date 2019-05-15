package expression.operation;

import execute.Scope;
import expression.value.LBoolean;
import expression.value.Value;
import parse.Token;
import parse.TokenLine;

import java.util.List;

/**
 * @since 2/22/2019
 */
public class NotOperation extends Operation {

    public NotOperation(int position, List<Token> tokens) {
        super(position, tokens);
    }

    @Override
    protected void parse(int pos, TokenLine tokens) {
        parseLeftUnaryOperation(pos, tokens);
    }

    @Override
    public Value evaluate(Scope scope) {
        return LBoolean.resolve(operands.get(0).evaluate(scope).asBoolean());
    }

}
