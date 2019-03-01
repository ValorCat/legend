package statement;

import execute.Environment;
import expression.Expression;
import parse.Parser;
import parse.Token;
import parse.Token.TokenType;
import parse.error.ErrorLog;

import java.util.List;

import static parse.error.ErrorDescription.BAD_ASSIGN;

/**
 * @since 1/19/2019
 */
public class Assignment implements Statement {

    private String target;
    private Expression value;

    public Assignment(List<Token> tokens, int pos, Parser parser) {
        if (pos == 0 || tokens.get(pos - 1).TYPE != TokenType.IDENTIFIER) {
            throw ErrorLog.raise(BAD_ASSIGN, "Missing assignment target on left of '='");
        } else if (pos == tokens.size() - 1 || !tokens.get(pos + 1).isValue()) {
            throw ErrorLog.raise(BAD_ASSIGN, "Missing assignment value on right of '='");
        }
        target = tokens.get(pos - 1).VALUE;
        value = parser.parseFrom(tokens, pos + 1);
    }

    @Override
    public void execute(Environment env) {
        env.assignLocal(target, value.evaluate(env));
    }

    @Override
    public String toString() {
        return "=(" + target + " " + value + ")";
    }

}
