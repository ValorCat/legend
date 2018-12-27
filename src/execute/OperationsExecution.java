package execute;

import dataformat.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * @since 12/23/2018
 */
public final class OperationsExecution {

    private OperationsExecution() {}

    public static final Map<String, OperationHandler> OPERATIONS = new HashMap<>();
    public interface OperationHandler extends BiFunction<List<Expression>, Environment, Value> {}

    static {
        OPERATIONS.put(".", OperationsExecution::getAttribute);
        OPERATIONS.put("call", OperationsExecution::call);
        OPERATIONS.put("to", OperationsExecution::buildRange);
        OPERATIONS.put("=", OperationsExecution::assign);
    }

    private static Value assign(List<Expression> operands, Environment env) {
        String name = operands.get(0).getIdentifier();
        Value value = operands.get(1).evaluate(env);
        env.assign(name, value);
        return value;
    }

    private static Value buildRange(List<Expression> operands, Environment env) {
        Value left = operands.get(0).evaluate(env);
        Value right = operands.get(1).evaluate(env);
        if (!left.type().matches("int") || !right.type().matches("int")) {
            throw new RuntimeException("Operator 'to' expected (int, int) but got (" + left.type() + ", "
                    + right.type() + "' instead");
        }
        return StandardLibrary.type("range").create(left, right);
    }

    private static Value call(List<Expression> operands, Environment env) {
        Value executable = operands.get(0).evaluate(env);
        ArgumentList arguments = new ArgumentList(operands.get(1), env);
        if (executable.hasOwner()) {
            arguments.setTarget(executable.getOwner());
            executable = ((Attribute) executable).getValue();
        }
        if (executable.type().matches("func")) {
            FunctionValue func = ((FunctionValue) executable);
            // todo check if args match params
            return func.call(arguments, env);
        } else if (executable.type().matches("type")) {
            return ((Type) executable).instantiate(arguments, env);
        }
        throw new RuntimeException("Cannot execute object of type '" + executable.type().getName() + "'");
    }

    private static Value getAttribute(List<Expression> operands, Environment env) {
        String attribute = operands.get(1).getIdentifier();
        if (operands.get(0).matches("_")) {
            return new FunctionValue((args, _env) -> args.args()[0].getAttribute(attribute));
        } else {
            Value target = operands.get(0).evaluate(env);
            return new Attribute(target, target.getAttribute(attribute));
        }
    }

}
