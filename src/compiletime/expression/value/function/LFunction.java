package compiletime.expression.value.function;

import compiletime.expression.group.ArgumentList;
import compiletime.expression.value.Value;
import runtime.Scope;

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

    public abstract Value call(ArgumentList args, Scope scope);
    public abstract boolean equals(Value other);
    public abstract String toString();

    @Override
    public String asString() {
        return "function[" + name + "]";
    }

    public Value call(Scope scope, Value... args) {
        return call(new ArgumentList(args), scope);
    }

    public String getName() {
        return name;
    }

}
