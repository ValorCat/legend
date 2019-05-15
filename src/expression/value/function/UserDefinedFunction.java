package expression.value.function;

import execute.Scope;
import expression.group.ArgumentList;
import expression.value.Value;
import statement.structure.FunctionDefinition;

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
    public Value call(ArgumentList args, Scope scope) {
        return body.call(args);
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
