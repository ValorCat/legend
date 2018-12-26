package dataformat;

import execute.Environment;

import java.util.function.BiFunction;

/**
 * @since 12/24/2018
 */
public class FunctionValue extends Value {

    public interface FunctionBody extends BiFunction<ArgumentList, Environment, Value> {}

    private String name;
    private FunctionBody body;

    public FunctionValue(FunctionBody body) {
        this("(anonymous function)", body);
    }

    public FunctionValue(String name, FunctionBody body) {
        super(Environment.FUNC_TYPE);
        this.name = name;
        this.body = body;
    }

    public String getName() {
        return name;
    }

    public Value call(ArgumentList args, Environment env) {
        return body.apply(args, env);
    }

    @Override
    public String toString() {
        return "func[" + Integer.toHexString(System.identityHashCode(body)) + "]";
    }
}
