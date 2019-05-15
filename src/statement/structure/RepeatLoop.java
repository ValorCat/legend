package statement.structure;

import execute.Program;
import execute.Scope;
import parse.Parser;
import parse.Token;
import parse.error.ErrorLog;

import java.util.List;

import static parse.error.ErrorDescription.BAD_REPEAT;

/**
 * @since 1/19/2019
 */
public class RepeatLoop implements FlowController {

    private int startIndex;

    public RepeatLoop(List<Token> tokens) {
        if (tokens.size() != 1) {
            throw ErrorLog.raise(BAD_REPEAT, "Unexpected symbol '%s' after 'repeat'", tokens.get(1));
        }
    }

    @Override
    public void execute(Scope scope) {
        Program.PROGRAM.getControlStack().push(this);
        startIndex = Program.PROGRAM.getCounter();
    }

    @Override
    public boolean isDone(Scope scope) {
        Program.PROGRAM.setCounter(startIndex + 1);
        return false;
    }

    @Override
    public void setJumpPoint(List<Token> tokens, Parser parser) {
        if (!tokens.get(0).matches("end")) {
            FlowController.invalidJumpPoint(tokens.get(0));
        }
    }

    @Override
    public String getKeyword() {
        return "repeat";
    }

    @Override
    public String toString() {
        return "repeat()";
    }

}
