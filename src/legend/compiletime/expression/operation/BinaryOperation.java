package legend.compiletime.expression.operation;

import legend.compiletime.Token;
import legend.compiletime.TokenLine;
import legend.compiletime.expression.Expression;
import legend.compiletime.expression.value.Value;
import legend.runtime.Scope;

import java.util.List;

public class BinaryOperation extends Operation {

    protected Expression left, right;

    public BinaryOperation(String operator, Expression left, Expression right) {
        super(operator);
        this.left = left;
        this.right = right;
    }

    @Override
    public void parse(TokenLine line, int operIndex) {
        line.consolidate(Token.newExpression(operator, this), operIndex - 1, 3);
    }

    @Override
    public Value evaluate(Scope scope) {
        Value leftValue = left.evaluate(scope);
        Value rightValue = right.evaluate(scope);
        return leftValue.operateBinary(operator, rightValue);
    }

    @Override
    public List<Expression> getChildren() {
        return List.of(left, right);
    }

}
