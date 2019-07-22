package legend.compiletime.expression.operation;

import legend.compiletime.Token;
import legend.compiletime.TokenLine;
import legend.compiletime.expression.Expression;

public class RightUnaryOperation extends UnaryOperation {

    public RightUnaryOperation(String operator, Expression operand) {
        super(operator, operand);
    }

    @Override
    public void parse(TokenLine line, int operIndex) {
        line.consolidate(Token.newExpression(operator, this), operIndex - 1, 2);
    }

}
