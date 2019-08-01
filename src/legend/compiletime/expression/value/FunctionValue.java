package legend.compiletime.expression.value;

import legend.compiletime.expression.group.ArgumentList;
import legend.runtime.Scope;

/**
 * @since 12/24/2018
 */
public abstract class FunctionValue extends Value {

    private String name;

    public FunctionValue() {
        this("anonymous function");
    }

    public FunctionValue(String name) {
        super("function");
        this.name = name;
    }

    public abstract Value call(ArgumentList args);
    public abstract boolean equals(Value other);
    public abstract String toString();

    @Override
    public String asString() {
        return "function[" + name + "]";
    }

    public Value call(Scope scope, Value... args) {
        return call(new ArgumentList(scope, args));
    }

    public String getName() {
        return name;
    }

}
