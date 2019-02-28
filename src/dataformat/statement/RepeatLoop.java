package dataformat.statement;

import execute.Environment;
import parse.Parser;
import parse.Token;

import java.util.List;

/**
 * @since 1/19/2019
 */
public class RepeatLoop implements FlowController {

    private int startIndex;

    public RepeatLoop(List<Token> tokens) {
        if (tokens.size() != 1) {
            throw new RuntimeException("Unexpected symbol '" + tokens.get(1).VALUE + "' after 'repeat'");
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
            throw new RuntimeException("Unexpected symbol '" + tokens.get(0).VALUE + "'");
        }
    }

    @Override
    public String toString() {
        return "repeat()";
    }

}
