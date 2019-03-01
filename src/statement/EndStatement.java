package statement;

import execute.Environment;
import parse.Parser;
import parse.Token;
import parse.error.ErrorLog;
import statement.structure.FlowController;

import java.util.List;
import java.util.Stack;

import static parse.error.ErrorDescription.BAD_NESTING;

/**
 * @since 1/19/2019
 */
public class EndStatement implements Statement {

    public EndStatement(List<Token> tokens, Parser parser) {
        if (tokens.size() != 1) {
            throw ErrorLog.raise(BAD_NESTING, "Unexpected symbol '%s' after 'end'", tokens.get(1));
        } else if (parser.getControlStack().isEmpty()) {
            throw ErrorLog.raise(BAD_NESTING, "Mismatched 'end'");
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
