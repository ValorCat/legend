package statement;

import execute.Program;
import execute.Scope;
import expression.Expression;
import expression.value.LNull;
import parse.Token;
import parse.error.ErrorLog;

import java.util.List;

import static parse.error.ErrorDescription.BAD_RETURN;

/**
 * @since 2/16/2019
 */
public class ReturnStatement implements Statement {

    private Expression value;

    public ReturnStatement(List<Token> tokens) {
        if (tokens.size() > 1 && !tokens.get(1).isValue()) {
            throw ErrorLog.raise(BAD_RETURN, "Expected expression after 'return'");
        }
        value = tokens.size() > 1
                ? tokens.get(1).asExpression()  // return with a value
                : LNull.NULL;                   // return without a value
    }


    @Override
    public void execute(Scope scope) {
        scope.setReturnValue(value.evaluate(scope));
        Program.PROGRAM.setCounter(scope.getReturnAddress());
    }

    @Override
    public String toString() {
        return "return(" + value + ")";
    }

}
