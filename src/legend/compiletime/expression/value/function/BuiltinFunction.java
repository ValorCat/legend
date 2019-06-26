package legend.compiletime.expression.value.function;

import legend.compiletime.expression.group.ArgumentList;
import legend.compiletime.expression.value.Value;
import legend.runtime.Scope;

import java.util.function.BiFunction;

/**
 * @since 12/24/2018
 */
public class BuiltinFunction extends LFunction {

    @FunctionalInterface
    public interface FunctionBody extends BiFunction<ArgumentList, Scope, Value> {}

    private FunctionBody body;

    public BuiltinFunction(FunctionBody body) {
        super();
        this.body = body;
    }

    public BuiltinFunction(String name, FunctionBody body) {
        super(name);
        this.body = body;
    }

    public Value call(ArgumentList args, Scope scope) {
        return body.apply(args, scope);
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
