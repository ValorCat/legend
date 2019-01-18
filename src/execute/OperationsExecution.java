package execute;

import dataformat.ArgumentList;
import dataformat.Expression;
import dataformat.flow.FlowController;
import dataformat.flow.RepeatControl;
import dataformat.value.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
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
        OPERATIONS.put("^", OperationsExecution::exponentiate);
        OPERATIONS.put("*", OperationsExecution::multiply);
        OPERATIONS.put("/", OperationsExecution::divide);
        OPERATIONS.put("+", OperationsExecution::add);
        OPERATIONS.put("-", OperationsExecution::subtract);
        OPERATIONS.put("to", OperationsExecution::buildRange);
        OPERATIONS.put("<", OperationsExecution::lessThan);
        OPERATIONS.put("<=", OperationsExecution::lessThanEqualTo);
        OPERATIONS.put(">", OperationsExecution::greaterThan);
        OPERATIONS.put(">=", OperationsExecution::greaterThanEqualTo);
        OPERATIONS.put("==", OperationsExecution::equals);
        OPERATIONS.put("!=", OperationsExecution::notEquals);
        OPERATIONS.put("=", OperationsExecution::assign);
        OPERATIONS.put("end", OperationsExecution::end);
        OPERATIONS.put("repeat", OperationsExecution::repeat);
    }

    private static Value add(List<Expression> operands, Environment env) {
        int left = operands.get(0).evaluate(env).asInt();
        int right = operands.get(1).evaluate(env).asInt();
        return new IntValue(left + right);
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
        if (!left.isType("int") || !right.isType("int")) {
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
        if (executable.isType("func")) {
            FunctionValue func = ((FunctionValue) executable);
            // todo check if args match params
            return func.call(arguments, env);
        } else if (executable.isType("type")) {
            return ((Type) executable).instantiate(arguments, env);
        }
        throw new RuntimeException("Cannot execute object of type '" + executable.type().getName() + "'");
    }

    private static Value divide(List<Expression> operands, Environment env) {
        int left = operands.get(0).evaluate(env).asInt();
        int right = operands.get(1).evaluate(env).asInt();
        return new IntValue(Math.round((float) left / right));
    }

    private static Value end(List<Expression> operands, Environment env) {
        Stack<FlowController> stack = env.getControlStack();
        if (stack.isEmpty()) {
            throw new RuntimeException("Unexpected 'end'");
        } else if (stack.peek().onEnd(env)) {
            stack.pop();
        }
        return new IntValue(0);
    }

    private static Value equals(List<Expression> operands, Environment env) {
        Value left = operands.get(0).evaluate(env);
        Value right = operands.get(1).evaluate(env);
        if (left.equals(right)) return BoolValue.TRUE;
        List<String> types = List.of(left.type().getName(), right.type().getName());
        if (types.contains("str") && (types.contains("bool") || types.contains("int"))) {
            return BoolValue.resolve(left.asStr().equals(right.asStr()));
        }
        return BoolValue.FALSE;
    }

    private static Value exponentiate(List<Expression> operands, Environment env) {
        int left = operands.get(0).evaluate(env).asInt();
        int right = operands.get(1).evaluate(env).asInt();
        return new IntValue((int) Math.pow(left, right));
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

    private static Value greaterThan(List<Expression> operands, Environment env) {
        int left = operands.get(0).evaluate(env).asInt();
        int right = operands.get(1).evaluate(env).asInt();
        return BoolValue.resolve(left > right);
    }

    private static Value greaterThanEqualTo(List<Expression> operands, Environment env) {
        int left = operands.get(0).evaluate(env).asInt();
        int right = operands.get(1).evaluate(env).asInt();
        return BoolValue.resolve(left >= right);
    }

    private static Value lessThan(List<Expression> operands, Environment env) {
        int left = operands.get(0).evaluate(env).asInt();
        int right = operands.get(1).evaluate(env).asInt();
        return BoolValue.resolve(left < right);
    }

    private static Value lessThanEqualTo(List<Expression> operands, Environment env) {
        int left = operands.get(0).evaluate(env).asInt();
        int right = operands.get(1).evaluate(env).asInt();
        return BoolValue.resolve(left <= right);
    }

    private static Value multiply(List<Expression> operands, Environment env) {
        int left = operands.get(0).evaluate(env).asInt();
        int right = operands.get(1).evaluate(env).asInt();
        return new IntValue(left * right);
    }

    private static Value notEquals(List<Expression> operands, Environment env) {
        Value left = operands.get(0).evaluate(env);
        Value right = operands.get(1).evaluate(env);
        if (left.equals(right)) return BoolValue.FALSE;
        List<String> types = List.of(left.type().getName(), right.type().getName());
        if (types.contains("str") && (types.contains("bool") || types.contains("int"))) {
            return BoolValue.resolve(!left.asStr().equals(right.asStr()));
        }
        return BoolValue.TRUE;
    }

    private static Value repeat(List<Expression> operands, Environment env) {
        env.getControlStack().push(new RepeatControl(env));
        return new IntValue(0);
    }

    private static Value subtract(List<Expression> operands, Environment env) {
        int left = operands.get(0).evaluate(env).asInt();
        int right = operands.get(1).evaluate(env).asInt();
        return new IntValue(left - right);
    }

}
