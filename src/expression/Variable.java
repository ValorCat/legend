package expression;

import execute.Scope;
import expression.value.Value;

/**
 * @since 12/23/2018
 */
public class Variable implements Expression {

    private String name;

    public Variable(String name) {
        this.name = name;
    }

    @Override
    public Value evaluate(Scope scope) {
        return scope.getVariableValue(name);
    }

    @Override
    public boolean matches(String pattern) {
        return name.equals(pattern);
    }

    @Override
    public String getIdentifier() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
