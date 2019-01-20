package dataformat.operation.function;

import dataformat.ArgumentList;
import dataformat.operation.Operation;
import dataformat.value.Value;
import execute.Environment;
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
    public Value evaluate(Environment env) {
        // X unop Y is equivalent to X.Y()

        Value target = operands.get(0).evaluate(env);
        String attribute = operands.get(1).getIdentifier();

        Value executable = target.getAttribute(attribute);
        ArgumentList arguments = new ArgumentList();
        arguments.setTarget(target);

        return FunctionCall.call(executable, arguments, env);
    }

}
