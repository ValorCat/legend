package statement;

import execute.Environment;
import expression.Expression;
import expression.value.LNull;
import parse.ParserError;
import parse.Token;

import java.util.List;

import static parse.ErrorDescription.BAD_RETURN;

/**
 * @since 2/16/2019
 */
public class ReturnStatement implements Statement {

    private Expression value;

    public ReturnStatement(List<Token> tokens) {
        if (tokens.size() > 1 && !tokens.get(1).isValue()) {
            throw ParserError.error(BAD_RETURN, "Expected expression after 'return'");
        }
        value = tokens.size() > 1
                ? tokens.get(1).asExpression()  // return with a value
                : LNull.NULL;                   // return without a value
    }


    @Override
    public void execute(Environment env) {
        env.setReturnValue(value.evaluate(env));
        env.setCounter(Integer.MAX_VALUE);
    }

    @Override
    public String toString() {
        return "return(" + value + ")";
    }

}
