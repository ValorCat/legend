package legend.compiletime.expression.operation;

import legend.compiletime.Token;
import legend.compiletime.TokenLine;
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
    public void parse(TokenLine line, int operIndex) {
        line.consolidate(Token.newExpression(operator, this), operIndex - 1, 3);
    }

    @Override
    public Value evaluate(Scope scope) {
        throw new RuntimeException("Unexpected ',' in expression");
    }

    @Override
    public List<Expression> getChildren() {
        return values;
    }

}
