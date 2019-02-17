package dataformat.operation;

import dataformat.Expression;
import dataformat.value.Value;
import execute.Environment;
import parse.Token;

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
    protected void parse(int pos, List<Token> tokens) {
        Token left = null, right = null;
        if (pos > 0) left = tokens.get(pos - 1);
        if (pos < tokens.size() - 1) right = tokens.get(pos + 1);
        if (left == null || right == null || !left.isValue() || !right.isValue()) {
            throw new RuntimeException("Operator ':=' requires values on both sides");
        }
        target = left.asExpression().getIdentifier();
        value = right.asExpression();
        Token.consolidate(tokens, Token.newExpression(":=", this), pos - 1, 3);
    }

    @Override
    public Value evaluate(Environment env) {
        Value result = value.evaluate(env);
        env.assign(target, result);
        return result;
    }

}
