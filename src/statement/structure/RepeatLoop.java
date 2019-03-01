package statement.structure;

import execute.Environment;
import parse.Parser;
import parse.ParserError;
import parse.Token;

import java.util.List;

import static parse.ErrorDescription.BAD_REPEAT;

/**
 * @since 1/19/2019
 */
public class RepeatLoop implements FlowController {

    private int startIndex;

    public RepeatLoop(List<Token> tokens) {
        if (tokens.size() != 1) {
            throw ParserError.error(BAD_REPEAT, "Unexpected symbol '%s' after 'repeat'", tokens.get(1));
        }
    }

    @Override
    public void execute(Environment env) {
        env.getControlStack().push(this);
        startIndex = env.getCounter();
    }

    @Override
    public boolean isDone(Environment env) {
        env.setCounter(startIndex + 1);
        return false;
    }

    @Override
    public void setJumpPoint(List<Token> tokens, Parser parser) {
        if (!tokens.get(0).matches("end")) {
            FlowController.invalidJumpPoint(tokens.get(0));
        }
    }

    @Override
    public String toString() {
        return "repeat()";
    }

}
