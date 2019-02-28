package expression.value;

import expression.Expression;
import execute.Environment;

/**
 * @since 12/23/2018
 */
public class Variable implements Expression {

    private String name;

    public Variable(String name) {
        this.name = name;
    }

    @Override
    public Value evaluate(Environment env) {
        return env.fetch(name);
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
