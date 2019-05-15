package statement;

import execute.Scope;
import parse.Token;
import parse.error.ErrorLog;

import java.util.List;

import static parse.error.ErrorDescription.BAD_PRINT_STRING;

public class PrintString implements Statement {

    private String content;

    public PrintString(List<Token> tokens, int pos) {
        if (pos != 0 || tokens.size() > 1) {
            throw ErrorLog.raise(BAD_PRINT_STRING, "Print string cannot be used in an expression (did you " +
                    "mean single quotes?)");
        }
        content = tokens.get(0).VALUE;
    }

    @Override
    public void execute(Scope scope) {
        System.out.println(content);
    }

}
