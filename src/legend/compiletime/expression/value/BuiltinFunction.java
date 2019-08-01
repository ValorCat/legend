package legend.compiletime.expression.value;

import legend.compiletime.expression.group.ArgumentList;

import java.util.function.Function;

/**
 * @since 12/24/2018
 */
public class BuiltinFunction extends FunctionValue {

    @FunctionalInterface
    public interface FunctionBody extends Function<ArgumentList, Value> {}

    private FunctionBody body;

    public BuiltinFunction(FunctionBody body) {
        super();
        this.body = body;
    }

    public BuiltinFunction(String name, FunctionBody body) {
        super(name);
        this.body = body;
    }

    @Override
    public Value call(ArgumentList args) {
        return body.apply(args);
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
