package dataformat.operation;

import dataformat.Expression;
import dataformat.value.LNull;
import dataformat.value.Value;
import execute.Environment;
import parse.Token;

import java.util.List;

/**
 * @since 1/19/2019
 */
public class Assignment extends Operation {

    private String target;
    private Expression value;

    public Assignment(int position, List<Token> tokens) {
        super(position, tokens);
    }

    @Override
    protected void parse(int pos, List<Token> tokens) {
        Token left = null, right = null;
        if (pos > 0) left = tokens.get(pos - 1);
        if (pos < tokens.size() - 1) right = tokens.get(pos + 1);
        if (left == null || right == null || !left.isValue() || !right.isValue()) {
            throw new RuntimeException("Operator '=' requires values on both sides (did you mean '==' or ':='?)");
        }
        target = left.asExpression().getIdentifier();
        value = right.asExpression();
        Token.consolidate(tokens, new Token("=", this), pos - 1, 3);
    }

    @Override
    public Value evaluate(Environment env) {
        env.assign(target, value.evaluate(env));
        return LNull.NULL;
    }

}
