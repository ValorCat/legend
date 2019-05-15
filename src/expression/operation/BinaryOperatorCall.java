package expression.operation;

import execute.Scope;
import expression.group.ArgumentList;
import expression.value.Attribute;
import expression.value.Value;
import parse.Token;

import java.util.List;

/**
 * @since 1/19/2019
 */
public class BinaryOperatorCall extends Operation {

    public BinaryOperatorCall(int position, List<Token> tokens) {
        super(position, tokens);
    }

    @Override
    public Value evaluate(Scope scope) {
        // X biop Y is equivalent to X(Y)

        Value executable = operands.get(0).evaluate(scope);
        Value arg = operands.get(1).evaluate(scope);
        ArgumentList arguments = new ArgumentList(arg);

        if (executable.hasOwner()) {
            arguments.setTarget(executable.getOwner());
            executable = ((Attribute) executable).getValue();
        }

        return FunctionCall.call(executable, arguments, scope);
    }

}
