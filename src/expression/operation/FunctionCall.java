package expression.operation;

import execute.Environment;
import expression.group.ArgumentList;
import expression.group.Parentheses;
import expression.value.Attribute;
import expression.value.Value;
import expression.value.function.LFunction;
import expression.value.type.Type;
import parse.Token;

import java.util.List;

/**
 * @since 1/18/2019
 */
public class FunctionCall extends Operation {

    public FunctionCall(int position, List<Token> tokens) {
        super(position, tokens);
    }

    @Override
    public Value evaluate(Environment env) {
        Value executable = operands.get(0).evaluate(env);
        ArgumentList arguments = new ArgumentList((Parentheses) operands.get(1), env);
        if (executable.hasOwner()) {
            arguments.setTarget(executable.getOwner());
            executable = ((Attribute) executable).getValue();
        }
        return call(executable, arguments, env);
    }

    public static Value call(Value executable, ArgumentList args, Environment env) {
        if (executable.isType("Function")) {
            return ((LFunction) executable).call(args, env);
        } else if (executable.isType("Type")) {
            return ((Type) executable).instantiate(args, env);
        }
        throw new RuntimeException("Cannot execute object of type '" + executable.type().getName() + "'");
    }

}
