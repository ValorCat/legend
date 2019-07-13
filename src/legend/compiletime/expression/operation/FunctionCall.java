package legend.compiletime.expression.operation;

import legend.compiletime.Token;
import legend.compiletime.expression.group.ArgumentList;
import legend.compiletime.expression.group.Parentheses;
import legend.compiletime.expression.value.Attribute;
import legend.compiletime.expression.value.Value;
import legend.compiletime.expression.value.function.LFunction;
import legend.compiletime.expression.value.type.Type;
import legend.runtime.Scope;

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
        ArgumentList arguments = new ArgumentList(scope, (Parentheses) operands.get(1));
        if (executable.hasOwner()) {
            arguments.setTarget(executable.getOwner());
            executable = ((Attribute) executable).getValue();
        }
        return call(executable, arguments);
    }

    public static Value call(Value executable, ArgumentList args) {
        if (executable.isType("Function")) {
            return ((LFunction) executable).call(args);
        } else if (executable.isType("Type")) {
            return ((Type) executable).instantiate(args);
        }
        throw new RuntimeException("Cannot execute object of type '" + executable.type().getName() + "'");
    }

}
