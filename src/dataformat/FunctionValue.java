package dataformat;

import execute.Environment;
import execute.StandardLibrary;

import java.util.function.BiFunction;

/**
 * @since 12/24/2018
 */
public class FunctionValue extends Value {

    @FunctionalInterface
    public interface FunctionBody extends BiFunction<ArgumentList, Environment, Value> {}

    private String name;
    private FunctionBody body;

    public FunctionValue(FunctionBody body) {
        this("anonymous function", body);
    }

    public FunctionValue(String name, FunctionBody body) {
        super(StandardLibrary.type("func"));
        this.name = name;
        this.body = body;
    }

    @Override
    public String asStr() {
        return "func[" + name + "]";
    }

    public String getName() {
        return name;
    }

    public Value call(ArgumentList args, Environment env) {
        return body.apply(args, env);
    }

    public Value call(Environment env, Value... args) {
        return body.apply(new ArgumentList(args), env);
    }

    @Override
    public boolean equals(Value other) {
        return type() == other.type()
                && body == ((FunctionValue) other).body;
    }

    @Override
    public String toString() {
        return "func[" + Integer.toHexString(System.identityHashCode(body)) + "]";
    }
}
