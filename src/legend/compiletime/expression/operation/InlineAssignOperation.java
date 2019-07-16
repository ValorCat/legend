package legend.compiletime.expression.operation;

import legend.compiletime.Token;
import legend.compiletime.TokenLine;
import legend.compiletime.expression.Expression;
import legend.compiletime.expression.Variable;
import legend.compiletime.expression.value.Value;
import legend.runtime.Scope;

import java.util.List;

/**
 * @since 2/9/2019
 */
public class InlineAssignOperation extends Operation {

    private static final String OPERATOR = ":=";

    private String target;
    private Expression value;

    public InlineAssignOperation(String target, Expression value) {
        super(OPERATOR);
        this.target = target;
        this.value = value;
    }

    @Override
    public Value evaluate(Scope scope) {
        Value result = value.evaluate(scope);
        scope.setVariable(target, result);
        return result;
    }

    @Override
    public List<Expression> getChildren() {
        return List.of(new Variable(target), value);
    }

    public static void parse(int operIndex, TokenLine line) {
        Token left = null, right = null;
        if (operIndex > 0) left = line.get(operIndex - 1);
        if (operIndex < line.size() - 1) right = line.get(operIndex + 1);
        if (left == null || right == null || !left.isValue() || !right.isValue()) {
            throw new RuntimeException("Operator ':=' requires values on both sides");
        }
        String target = left.asExpression().getIdentifier();
        Expression value = right.asExpression();

        InlineAssignOperation operation = new InlineAssignOperation(target, value);
        line.consolidate(Token.newExpression(OPERATOR, operation), operIndex - 1, 3);
    }

}
