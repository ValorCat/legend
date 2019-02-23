package dataformat.operation;

import dataformat.value.LBoolean;
import dataformat.value.Value;
import execute.Environment;
import parse.Token;

import java.util.List;

/**
 * @since 2/22/2019
 */
public class NotOperation extends Operation {

    public NotOperation(int position, List<Token> tokens) {
        super(position, tokens);
    }

    @Override
    protected void parse(int pos, List<Token> tokens) {
        parseLeftUnaryOperation(pos, tokens);
    }

    @Override
    public Value evaluate(Environment env) {
        return LBoolean.resolve(operands.get(0).evaluate(env).asBoolean());
    }

}
