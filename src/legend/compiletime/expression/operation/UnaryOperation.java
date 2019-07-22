package legend.compiletime.expression.operation;

import legend.compiletime.expression.Expression;
import legend.compiletime.expression.value.Value;
import legend.runtime.Scope;

import java.util.List;

public abstract class UnaryOperation extends Operation {

    private Expression operand;

    public UnaryOperation(String operator, Expression operand) {
        super(operator);
        this.operand = operand;
    }

    @Override
    public Value evaluate(Scope scope) {
        Value value = operand.evaluate(scope);
        return value.operateUnary(operator);
    }

    @Override
    public List<Expression> getChildren() {
        return List.of(operand);
    }

}
