package legend.compiletime.expression.operation;

import legend.compiletime.Token;
import legend.compiletime.TokenLine;
import legend.compiletime.error.ErrorLog;
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
    public Value evaluate(Scope scope) {
        Value leftValue = left.evaluate(scope);
        Value rightValue = right.evaluate(scope);
        return leftValue.operateBinary(operator, rightValue);
    }

    @Override
    public List<Expression> getChildren() {
        return List.of(left, right);
    }

    public static void parse(int operIndex, TokenLine line) {
        String operator = line.get(operIndex).VALUE;

        // check that there are values on each side
        Token left = null, right = null;
        if (operIndex > 0) left = line.get(operIndex - 1);
        if (operIndex < line.size() - 1) right = line.get(operIndex + 1);
        if (left == null || right == null || !left.isValue() || !right.isValue()) {
            throw ErrorLog.get("The '%s' operator requires values on both sides", operator);
        }

        // convert this operation into a tree
        BinaryOperation operation = new BinaryOperation(operator, left.asExpression(), right.asExpression());
        line.consolidate(Token.newExpression(operator, operation), operIndex - 1, 3);
    }

}
