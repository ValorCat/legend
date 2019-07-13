package legend.compiletime.expression.value.function;

import legend.compiletime.expression.group.ArgumentList;
import legend.compiletime.expression.value.Value;
import legend.runtime.Scope;

/**
 * @since 12/24/2018
 */
public abstract class LFunction extends Value {

    private String name;

    public LFunction() {
        this("anonymous function");
    }

    public LFunction(String name) {
        super("Function");
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
