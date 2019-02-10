package dataformat.operation.flow;

import dataformat.operation.Operation;
import dataformat.value.NullValue;
import dataformat.value.Value;
import execute.Environment;
import parse.Token;

import java.util.List;
import java.util.Stack;

/**
 * @since 1/19/2019
 */
public class EndStatement extends Operation {

    public EndStatement(int position, List<Token> tokens, int address, Stack<FlowController> controlStack) {
        super(position, tokens);
        if (controlStack.isEmpty()) {
            throw new RuntimeException("Unexpected 'end'");
        }
        controlStack.pop().setJumpPoint(address, position, tokens);
    }

    @Override
    protected void parse(int pos, List<Token> tokens) {
        parseStandaloneOperation(pos, tokens);
    }

    @Override
    public Value evaluate(Environment env) {
        Stack<FlowController> stack = env.getControlStack();
        if (stack.peek().isDone(env)) {
            stack.pop();
        }
        return NullValue.NULL;
    }

}
