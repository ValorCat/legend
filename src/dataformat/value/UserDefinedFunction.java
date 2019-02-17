package dataformat.value;

import dataformat.ArgumentList;
import dataformat.operation.function.FunctionDefinition;
import execute.Environment;

/**
 * @since 2/16/2019
 */
public class UserDefinedFunction extends LFunction {

    private FunctionDefinition body;

    public UserDefinedFunction(String name, FunctionDefinition body) {
        super(name);
        this.body = body;
    }

    @Override
    public Value call(ArgumentList args, Environment env) {
        return body.call(args, env);
    }

    @Override
    public boolean equals(Value other) {
        return other instanceof UserDefinedFunction && body == ((UserDefinedFunction) other).body;
    }

    @Override
    public String toString() {
        return "function[" + getName() + "]";
    }

}
