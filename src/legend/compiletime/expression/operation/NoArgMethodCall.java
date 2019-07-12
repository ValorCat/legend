package legend.compiletime.expression.operation;

import legend.compiletime.expression.Expression;
import legend.compiletime.expression.Variable;
import legend.compiletime.expression.group.ArgumentList;
import legend.compiletime.expression.value.Value;
import legend.runtime.Scope;

import java.util.List;

/**
 * @since 1/19/2019
 */
public class NoArgMethodCall extends Operation {

    public NoArgMethodCall(Expression target, String method) {
        super("noargmeth", List.of(target, new Variable(method)));
    }

    @Override
    public Value evaluate(Scope scope) {
        Value target = operands.get(0).evaluate(scope);
        String attribute = operands.get(1).getIdentifier();

        Value executable = target.getAttribute(attribute);
        ArgumentList arguments = new ArgumentList();
        arguments.setTarget(target);

        return FunctionCall.call(executable, arguments, scope);
    }

}
