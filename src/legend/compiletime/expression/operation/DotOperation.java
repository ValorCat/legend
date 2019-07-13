package legend.compiletime.expression.operation;

import legend.compiletime.Token;
import legend.compiletime.TokenLine;
import legend.compiletime.expression.Expression;
import legend.compiletime.expression.value.Attribute;
import legend.compiletime.expression.value.Value;
import legend.compiletime.expression.value.function.BuiltinFunction;
import legend.runtime.Scope;

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
            return new BuiltinFunction(args -> args.arg(0).getAttribute(attribute));
        } else {
            Value target = operands.get(0).evaluate(scope);
            return new Attribute(target, target.getAttribute(attribute));
        }
    }

}
