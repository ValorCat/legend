package compiletime.expression.operation;

import compiletime.Token;
import compiletime.expression.group.ArgumentList;
import compiletime.expression.value.Attribute;
import compiletime.expression.value.Value;
import runtime.Scope;

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
