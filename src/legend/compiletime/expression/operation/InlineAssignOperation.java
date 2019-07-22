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
    public void parse(TokenLine line, int operIndex) {
        line.consolidate(Token.newExpression(operator, this), operIndex - 1, 3);
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

}
