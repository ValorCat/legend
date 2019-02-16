package dataformat.operation.flow;

import dataformat.operation.Operation;
import dataformat.value.LNull;
import dataformat.value.Value;
import execute.Environment;
import parse.Token;

import java.util.List;
import java.util.Stack;

/**
 * @since 1/19/2019
 */
public class RepeatStatement extends Operation implements FlowController {

    private int startIndex;

    public RepeatStatement(int position, List<Token> tokens, Stack<FlowController> controlStack) {
        super(position, tokens);
        controlStack.push(this);
    }

    @Override
    protected void parse(int pos, List<Token> tokens) {
        parseStandaloneOperation(pos, tokens);
    }

    @Override
    public Value evaluate(Environment env) {
        env.getControlStack().push(this);
        startIndex = env.getCounter();
        return LNull.NULL;
    }

    @Override
    public boolean isDone(Environment env) {
        env.setCounter(startIndex + 1);
        return false;
    }

    @Override
    public void setJumpPoint(int address, int tokenPos, List<Token> statement) {
        if (!statement.get(tokenPos).matches("end")) {
            throw new RuntimeException("Unexpected symbol '" + statement.get(0).VALUE + "'");
        }
    }

}
