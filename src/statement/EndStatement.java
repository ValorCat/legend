package statement;

import execute.Environment;
import parse.Parser;
import parse.Token;
import statement.structure.FlowController;

import java.util.List;
import java.util.Stack;

/**
 * @since 1/19/2019
 */
public class EndStatement implements Statement {

    public EndStatement(List<Token> tokens, Parser parser) {
        if (tokens.size() != 1) {
            throw new RuntimeException("Unexpected symbol '" + tokens.get(1).VALUE + "' after 'end'");
        } else if (parser.getControlStack().isEmpty()) {
            throw new RuntimeException("Unexpected 'end'");
        }
        FlowController controlStruct = parser.getControlStack().pop();
        controlStruct.setJumpPoint(tokens, parser);
    }

    @Override
    public void execute(Environment env) {
        Stack<FlowController> stack = env.getControlStack();
        if (stack.peek().isDone(env)) {
            stack.pop();
        }
    }

}
