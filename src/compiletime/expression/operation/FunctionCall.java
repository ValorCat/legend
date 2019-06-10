package compiletime.expression.operation;

import compiletime.Token;
import compiletime.expression.group.ArgumentList;
import compiletime.expression.group.Parentheses;
import compiletime.expression.value.Attribute;
import compiletime.expression.value.Value;
import compiletime.expression.value.function.LFunction;
import compiletime.expression.value.type.Type;
import runtime.Scope;

import java.util.List;

/**
 * @since 1/18/2019
 */
public class FunctionCall extends Operation {

    public FunctionCall(int position, List<Token> tokens) {
        super(position, tokens);
    }

    @Override
    public Value evaluate(Scope scope) {
        Value executable = operands.get(0).evaluate(scope);
        ArgumentList arguments = new ArgumentList((Parentheses) operands.get(1), scope);
        if (executable.hasOwner()) {
            arguments.setTarget(executable.getOwner());
            executable = ((Attribute) executable).getValue();
        }
        return call(executable, arguments, scope);
    }

    public static Value call(Value executable, ArgumentList args, Scope scope) {
        if (executable.isType("Function")) {
            return ((LFunction) executable).call(args, scope);
        } else if (executable.isType("Type")) {
            return ((Type) executable).instantiate(args, scope);
        }
        throw new RuntimeException("Cannot execute object of type '" + executable.type().getName() + "'");
    }

}
