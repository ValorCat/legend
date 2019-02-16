package dataformat.value;

import dataformat.ArgumentList;
import execute.Environment;
import execute.StandardLibrary;

/**
 * @since 12/24/2018
 */
public abstract class LFunction extends Value {

    private String name;

    public LFunction() {
        this("anonymous function");
    }

    public LFunction(String name) {
        super(StandardLibrary.type("Function"));
        this.name = name;
    }

    public abstract Value call(ArgumentList args, Environment env);
    public abstract boolean equals(Value other);
    public abstract String toString();

    @Override
    public String asString() {
        return "function[" + name + "]";
    }

    public Value call(Environment env, Value... args) {
        return call(new ArgumentList(args), env);
    }

    public String getName() {
        return name;
    }

}
