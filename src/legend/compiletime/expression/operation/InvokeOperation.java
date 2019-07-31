package legend.compiletime.expression.operation;

import legend.compiletime.expression.Expression;
import legend.compiletime.expression.group.ArgumentList;
import legend.compiletime.expression.group.Parentheses;
import legend.compiletime.expression.value.Attribute;
import legend.compiletime.expression.value.Value;
import legend.compiletime.expression.value.function.LFunction;
import legend.compiletime.expression.value.type.Type;
import legend.runtime.Scope;

public class InvokeOperation extends BinaryOperation {

    private static final String OPERATOR = "()";

    public InvokeOperation(Expression callable, Parentheses args) {
        super(OPERATOR, callable, args);
    }

    @Override
    public Value evaluate(Scope scope) {
        Value callable = left.evaluate(scope);
        ArgumentList args = new ArgumentList(scope, (Parentheses) right);
        if (callable.hasOwner()) {
            args.setTarget(callable.getOwner());
            callable = ((Attribute) callable).getValue();
        }
        return call(callable, args);
    }

    private static Value call(Value callable, ArgumentList args) {
        if (callable.isType("function")) {
            return ((LFunction) callable).call(args);
        } else if (callable.isType("type")) {
            return ((Type) callable).instantiate(args);
        }
        throw new RuntimeException("Cannot invoke object of type '" + callable.type().getName() + "'");
    }

}
