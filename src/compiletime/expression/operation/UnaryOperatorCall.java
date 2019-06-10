package compiletime.expression.operation;

import compiletime.Token;
import compiletime.expression.Expression;
import compiletime.expression.Variable;
import compiletime.expression.group.ArgumentList;
import compiletime.expression.value.Value;
import runtime.Scope;

import java.util.List;

/**
 * @since 1/19/2019
 */
public class UnaryOperatorCall extends Operation {

    public UnaryOperatorCall(int position, List<Token> tokens) {
        super(position, tokens);
    }

    public UnaryOperatorCall(Expression target, String method) {
        super("unop", List.of(target, new Variable(method)));
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
