package expression.value.function;

import execute.Environment;
import expression.group.ArgumentList;
import expression.value.Value;

import java.util.function.BiFunction;

/**
 * @since 12/24/2018
 */
public class BuiltinFunction extends LFunction {

    @FunctionalInterface
    public interface FunctionBody extends BiFunction<ArgumentList, Environment, Value> {}

    private FunctionBody body;

    public BuiltinFunction(FunctionBody body) {
        super();
        this.body = body;
    }

    public BuiltinFunction(String name, FunctionBody body) {
        super(name);
        this.body = body;
    }

    public Value call(ArgumentList args, Environment env) {
        return body.apply(args, env);
    }

    @Override
    public boolean equals(Value other) {
        return other instanceof BuiltinFunction && body == ((BuiltinFunction) other).body;
    }

    @Override
    public String toString() {
        return "native_function[" + Integer.toHexString(System.identityHashCode(body)) + "]";
    }

}
