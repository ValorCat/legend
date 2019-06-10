package compiletime.expression.operation;

import compiletime.Token;
import compiletime.TokenLine;
import compiletime.expression.Expression;
import compiletime.expression.value.Value;
import runtime.Scope;

import java.util.List;

/**
 * @since 2/9/2019
 */
public class AssignmentExpression extends Operation {

    private String target;
    private Expression value;

    public AssignmentExpression(int position, List<Token> tokens) {
        super(position, tokens);
    }

    @Override
    protected void parse(int pos, TokenLine tokens) {
        Token left = null, right = null;
        if (pos > 0) left = tokens.get(pos - 1);
        if (pos < tokens.size() - 1) right = tokens.get(pos + 1);
        if (left == null || right == null || !left.isValue() || !right.isValue()) {
            throw new RuntimeException("Operator ':=' requires values on both sides");
        }
        target = left.asExpression().getIdentifier();
        value = right.asExpression();
        tokens.consolidate(Token.newExpression(":=", this), pos - 1, 3);
    }

    @Override
    public Value evaluate(Scope scope) {
        Value result = value.evaluate(scope);
        scope.setVariable(target, result);
        return result;
    }

}
