package compiletime.expression.operation;

import compiletime.Token;
import compiletime.TokenLine;
import compiletime.expression.group.ArgumentList;
import compiletime.expression.value.Value;
import runtime.Scope;

import java.util.List;

/**
 * @since 2/22/2019
 */
public class LengthOperation extends Operation {

    public LengthOperation(int position, List<Token> tokens) {
        super(position, tokens);
    }

    @Override
    protected void parse(int pos, TokenLine tokens) {
        parseLeftUnaryOperation(pos, tokens);
    }

    @Override
    public Value evaluate(Scope scope) {
        Value operand = operands.get(0).evaluate(scope);
        ArgumentList args = new ArgumentList();
        args.setTarget(operand);
        return operand.callMetamethod("_size", args, scope, "target of # operator");
    }

}
