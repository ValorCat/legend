package expression.value;

import expression.group.ArgumentList;
import execute.Environment;

import java.util.function.BiFunction;

/**
 * @since 12/24/2018
 */
public class NativeFunction extends LFunction {

    @FunctionalInterface
    public interface FunctionBody extends BiFunction<ArgumentList, Environment, Value> {}

    private FunctionBody body;

    public NativeFunction(FunctionBody body) {
        super();
        this.body = body;
    }

    public NativeFunction(String name, FunctionBody body) {
        super(name);
        this.body = body;
    }

    public Value call(ArgumentList args, Environment env) {
        return body.apply(args, env);
    }

    @Override
    public boolean equals(Value other) {
        return other instanceof NativeFunction && body == ((NativeFunction) other).body;
    }

    @Override
    public String toString() {
        return "native_function[" + Integer.toHexString(System.identityHashCode(body)) + "]";
    }

}
