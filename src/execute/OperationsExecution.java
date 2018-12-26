package execute;

import dataformat.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import static execute.Environment.*;

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
        if (!left.isType(INT_TYPE) || !right.isType(INT_TYPE)) {
            throw new RuntimeException("Operator 'to' expected (int, int) but got (" + left.type() + ", "
                    + right.type() + "' instead");
        }
        return RANGE_TYPE.create(left, right);
    }

    private static Value call(List<Expression> operands, Environment env) {
        Value target = operands.get(0).evaluate(env);
        ArgumentList arguments = new ArgumentList(operands.get(1), env);
        if (target.isType(FUNC_TYPE)) {
            FunctionValue funcObject = ((FunctionValue) target);
            // todo check if args match params
            return funcObject.call(arguments, env);
        } else if (target.isType(TYPE_TYPE)) {
            return ((Type) target).instantiate(arguments, env);
        }
        throw new RuntimeException("Cannot execute object of type '" + target.type().getName() + "'");
    }

    private static Value getAttribute(List<Expression> operands, Environment env) {
        String attribute = operands.get(1).getIdentifier();
        if (operands.get(0).matches("_")) {
            return new FunctionValue((args, _env) -> args.args()[0].getAttribute(attribute));
        } else {
            return operands.get(0).evaluate(env).getAttribute(attribute);
        }
    }

}
