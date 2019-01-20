package dataformat.operation.flow;

import dataformat.operation.Operation;
import dataformat.value.IntValue;
import dataformat.value.Value;
import execute.Environment;
import parse.Token;

import java.util.List;

/**
 * @since 1/19/2019
 */
public class RepeatStatement extends Operation implements FlowController {

    private int startIndex;

    public RepeatStatement(int position, List<Token> tokens) {
        super(position, tokens);
    }

    @Override
    protected void parse(int pos, List<Token> tokens) {
        parseStandaloneOperation(pos, tokens);
    }

    @Override
    public Value evaluate(Environment env) {
        env.getControlStack().push(this);
        startIndex = env.getCounter();
        return new IntValue(0);
    }

    @Override
    public void onBegin(Environment env) {}

    @Override
    public boolean onEnd(Environment env) {
        env.setCounter(startIndex);
        return false;
    }

}
