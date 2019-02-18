package dataformat.operation.flow;

import dataformat.Expression;
import dataformat.operation.Operation;
import dataformat.value.LNull;
import dataformat.value.Value;
import execute.Environment;
import parse.Token;

import java.util.List;

/**
 * @since 2/16/2019
 */
public class ReturnStatement extends Operation {

    private Expression value;

    public ReturnStatement(int position, List<Token> tokens) {
        super(position, tokens);
    }

    @Override
    protected void parse(int pos, List<Token> tokens) {
        if (pos > 0) {
            throw new RuntimeException("Unexpected symbol 'return'");
        } else if (tokens.size() > 1 && !tokens.get(1).isValue()) {
            throw new RuntimeException("Expected expression after 'return'");
        } else if (tokens.size() > 2) {
            throw new RuntimeException("Unexpected symbol '" + tokens.get(2).VALUE + "'");
        }
        value = tokens.size() == 2
                ? tokens.get(1).asExpression()  // return with a value
                : LNull.NULL;                   // return without a value
        Token.consolidate(tokens, Token.newStatement("return", this), 0, tokens.size());
    }

    @Override
    public Value evaluate(Environment env) {
        env.setReturnValue(value.evaluate(env));
        env.setCounter(Integer.MAX_VALUE);
        return LNull.NULL;
    }

    @Override
    public String toString() {
        return "return(" + value + ")";
    }

}
