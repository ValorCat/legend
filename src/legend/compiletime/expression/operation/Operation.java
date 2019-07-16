package legend.compiletime.expression.operation;

import legend.compiletime.expression.Expression;

import java.util.List;
import java.util.StringJoiner;

public abstract class Operation implements Expression {

    protected String operator;

    public Operation(String operator) {
        this.operator = operator;
    }

    @Override
    public abstract List<Expression> getChildren();

    @Override
    public boolean matches(String pattern) {
        return operator.equals(pattern);
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(" ", "(", ")");
        for (Expression operand : getChildren()) {
            joiner.add(operand.toString());
        }
        return operator + joiner;
    }

}
