package compiletime.expression.operation;

import compiletime.Token;
import compiletime.TokenLine;
import compiletime.expression.Expression;
import compiletime.expression.value.Attribute;
import compiletime.expression.value.Value;
import compiletime.expression.value.function.BuiltinFunction;
import runtime.Scope;

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
    public Value evaluate(Scope scope) {
        String attribute = operands.get(1).getIdentifier();
        if (operands.get(0).matches("_")) {
            return new BuiltinFunction((args, _scope) -> args.arg(0).getAttribute(attribute));
        } else {
            Value target = operands.get(0).evaluate(scope);
            return new Attribute(target, target.getAttribute(attribute));
        }
    }

}
