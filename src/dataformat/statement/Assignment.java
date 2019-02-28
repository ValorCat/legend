package dataformat.statement;

import dataformat.Expression;
import execute.Environment;
import parse.Parser;
import parse.Token;
import parse.Token.TokenType;

import java.util.List;

/**
 * @since 1/19/2019
 */
public class Assignment implements Statement {

    private String target;
    private Expression value;

    public Assignment(List<Token> tokens, int pos, Parser parser) {
        if (pos == 0 || tokens.get(pos - 1).TYPE != TokenType.IDENTIFIER) {
            throw new RuntimeException("Missing assignment target on left of '='");
        } else if (pos == tokens.size() - 1 || !tokens.get(pos + 1).isValue()) {
            throw new RuntimeException("Missing assignment value on right of '='");
        }
        target = tokens.get(pos - 1).VALUE;
        value = parser.parseFrom(tokens, pos + 1);
    }

    @Override
    public void execute(Environment env) {
        env.assign(target, value.evaluate(env));
    }

    @Override
    public String toString() {
        return "=(" + target + " " + value + ")";
    }

}
