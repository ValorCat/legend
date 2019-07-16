package legend.compiletime.expression.operation;

import legend.compiletime.Token;
import legend.compiletime.TokenLine;
import legend.compiletime.error.ErrorLog;
import legend.compiletime.expression.Expression;
import legend.compiletime.expression.value.Value;
import legend.runtime.Scope;

import java.util.ArrayList;
import java.util.List;

/**
 * @since 1/19/2019
 */
public class CommaOperation extends Operation {

    private static final String OPERATOR = ",";
    private List<Expression> values;

    public CommaOperation(Expression left, Expression right) {
        super(OPERATOR);
        if (left instanceof CommaOperation) {
            values = left.getChildren();
            values.add(right);
        } else {
            values = new ArrayList<>(List.of(left, right));
        }
    }

    @Override
    public Value evaluate(Scope scope) {
        throw new RuntimeException("Unexpected ',' in expression");
    }

    @Override
    public List<Expression> getChildren() {
        return values;
    }

    public static void parse(int operIndex, TokenLine line) {
        // check that there are values on each side
        Token left = null, right = null;
        if (operIndex > 0) left = line.get(operIndex - 1);
        if (operIndex < line.size() - 1) right = line.get(operIndex + 1);
        if (left == null || right == null || !left.isValue() || !right.isValue()) {
            throw ErrorLog.get("The '%s' operator requires values on both sides", OPERATOR);
        }

        // convert this operation into a tree
        CommaOperation operation = new CommaOperation(left.asExpression(), right.asExpression());
        line.consolidate(Token.newExpression(OPERATOR, operation), operIndex - 1, 3);
    }

}
