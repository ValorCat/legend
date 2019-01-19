package dataformat.operation;

import dataformat.value.Value;
import execute.Environment;
import parse.Token;

import java.util.List;

/**
 * @since 1/19/2019
 */
public class Assignment extends Operation {

    public Assignment(int position, List<Token> tokens) {
        super(position, tokens);
    }

    @Override
    protected void parse(int pos, List<Token> tokens) {
        super.parse(pos, tokens);

        // throws exception if left side is not an identifier
        // todo handle expressions in assignment lhs (eg x.y = z)
        operands.get(0).getIdentifier();
    }

    @Override
    public Value evaluate(Environment env) {
        String name = operands.get(0).getIdentifier();
        Value value = operands.get(1).evaluate(env);
        env.assign(name, value);
        return value;
    }

}
