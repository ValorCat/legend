package legend.compiletime.expression.operation;

import legend.compiletime.Token;
import legend.compiletime.TokenLine;
import legend.compiletime.error.ErrorLog;
import legend.compiletime.expression.Expression;
import legend.compiletime.expression.value.Value;
import legend.runtime.Scope;

import java.util.List;

public class UnaryOperation extends Operation {

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

    public static void parseLeft(int operIndex, TokenLine line) {
        String operator = line.get(operIndex).VALUE;
        if (operIndex == line.size() - 1 || !line.get(operIndex + 1).isValue()) {
            throw ErrorLog.get("The '%s' operator requires a value on the right", operator);
        }
        Token operand = line.get(operIndex + 1);

        // convert this operation into a tree
        UnaryOperation operation = new UnaryOperation(operator, operand.asExpression());
        line.consolidate(Token.newExpression(operator, operation), operIndex, 2);
    }

    public static void parseRight(int operIndex, TokenLine line) {
        String operator = line.get(operIndex).VALUE;
        if (operIndex == 0 || !line.get(operIndex - 1).isValue()) {
            throw ErrorLog.get("The '%s' operator requires a value on the left", operator);
        }
        Token operand = line.get(operIndex - 1);

        // convert this operation into a tree
        UnaryOperation operation = new UnaryOperation(operator, operand.asExpression());
        line.consolidate(Token.newExpression(operator, operation), operIndex - 1, 2);
    }

}
