package compiletime.expression.operation;

import compiletime.Token;
import compiletime.TokenLine;
import compiletime.expression.value.LBoolean;
import compiletime.expression.value.Value;
import runtime.Scope;

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
