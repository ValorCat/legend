package dataformat.operation;

import dataformat.ArgumentList;
import dataformat.value.Attribute;
import dataformat.value.Value;
import execute.Environment;
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
    public Value evaluate(Environment env) {
        // X biop Y is equivalent to X(Y)

        Value executable = operands.get(0).evaluate(env);
        Value arg = operands.get(1).evaluate(env);
        ArgumentList arguments = new ArgumentList(arg);

        if (executable.hasOwner()) {
            arguments.setTarget(executable.getOwner());
            executable = ((Attribute) executable).getValue();
        }

        return FunctionCall.call(executable, arguments, env);
    }

}
