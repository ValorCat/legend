package expression.operation;

import execute.Environment;
import expression.Expression;
import expression.value.Attribute;
import expression.value.Value;
import expression.value.function.BuiltinFunction;
import parse.Token;
import parse.TokenLine;

import java.util.List;

/**
 * @since 1/18/2019
 */
public class DotOperation extends Operation {

    public DotOperation(int position, List<Token> tokens) {
        super(position, tokens);
    }

    @Override
    protected void parse(int pos, TokenLine tokens) {
        super.parse(pos, tokens);

        // ensure that second operand is an identifier
        Expression dotOperation = tokens.get(pos - 1).EXPRESSION;
        dotOperation.getChildren().get(1).getIdentifier();
    }

    @Override
    public Value evaluate(Environment env) {
        String attribute = operands.get(1).getIdentifier();
        if (operands.get(0).matches("_")) {
            return new BuiltinFunction((args, _env) -> args.arg(0).getAttribute(attribute));
        } else {
            Value target = operands.get(0).evaluate(env);
            return new Attribute(target, target.getAttribute(attribute));
        }
    }

}
