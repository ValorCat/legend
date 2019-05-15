package expression.operation;

import execute.Scope;
import expression.group.ArgumentList;
import expression.value.Value;
import parse.Token;

import java.util.List;

/**
 * @since 1/19/2019
 */
public class UnaryOperatorCall extends Operation {

    public UnaryOperatorCall(int position, List<Token> tokens) {
        super(position, tokens);
    }

    @Override
    public Value evaluate(Scope scope) {
        // X unop Y is equivalent to X.Y()

        Value target = operands.get(0).evaluate(scope);
        String attribute = operands.get(1).getIdentifier();

        Value executable = target.getAttribute(attribute);
        ArgumentList arguments = new ArgumentList();
        arguments.setTarget(target);

        return FunctionCall.call(executable, arguments, scope);
    }

}
