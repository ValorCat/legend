package dataformat.operation;

import dataformat.Expression;
import dataformat.value.Attribute;
import dataformat.value.NativeFunction;
import dataformat.value.Value;
import execute.Environment;
import parse.Token;

import java.util.List;

/**
 * @since 1/18/2019
 */
public class DotOperation extends Operation {

    public DotOperation(int position, List<Token> tokens) {
        super(position, tokens);
    }

    @Override
    protected void parse(int pos, List<Token> tokens) {
        super.parse(pos, tokens);

        // ensure that second operand is an identifier
        Expression dotOperation = tokens.get(pos - 1).EXPRESSION;
        dotOperation.getChildren().get(1).getIdentifier();
    }

    @Override
    public Value evaluate(Environment env) {
        String attribute = operands.get(1).getIdentifier();
        if (operands.get(0).matches("_")) {
            return new NativeFunction((args, _env) -> args.args()[0].getAttribute(attribute));
        } else {
            Value target = operands.get(0).evaluate(env);
            return new Attribute(target, target.getAttribute(attribute));
        }
    }

}
