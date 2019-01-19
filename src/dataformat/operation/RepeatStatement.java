package dataformat.operation;

import dataformat.flow.RepeatControl;
import dataformat.value.IntValue;
import dataformat.value.Value;
import execute.Environment;
import parse.Token;

import java.util.List;

/**
 * @since 1/19/2019
 */
public class RepeatStatement extends Operation {

    public RepeatStatement(int position, List<Token> tokens) {
        super(position, tokens);
    }

    @Override
    protected void parse(int pos, List<Token> tokens) {
        parseStandaloneOperation(pos, tokens);
    }

    @Override
    public Value evaluate(Environment env) {
        env.getControlStack().push(new RepeatControl(env));
        return new IntValue(0);
    }

}
