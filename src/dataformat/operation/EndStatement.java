package dataformat.operation;

import dataformat.flow.FlowController;
import dataformat.value.IntValue;
import dataformat.value.Value;
import execute.Environment;
import parse.Token;

import java.util.List;
import java.util.Stack;

/**
 * @since 1/19/2019
 */
public class EndStatement extends Operation {

    public EndStatement(int position, List<Token> tokens) {
        super(position, tokens);
    }

    @Override
    protected void parse(int pos, List<Token> tokens) {
        parseStandaloneOperation(pos, tokens);
    }

    @Override
    public Value evaluate(Environment env) {
        Stack<FlowController> stack = env.getControlStack();
        if (stack.isEmpty()) {
            throw new RuntimeException("Unexpected 'end'");
        } else if (stack.peek().onEnd(env)) {
            stack.pop();
        }
        return new IntValue(0);
    }

}
