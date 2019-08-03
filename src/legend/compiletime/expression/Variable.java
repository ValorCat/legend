package legend.compiletime.expression;

import legend.compiletime.expression.value.Value;
import legend.runtime.Scope;

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
        return scope.getVariable(name);
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
