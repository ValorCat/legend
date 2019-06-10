package statement.block.clause;

import parse.Parser;
import parse.TokenLine;
import parse.error.ErrorLog;
import statement.Statement;

import static parse.error.ErrorDescription.BAD_IF;

public class ElseClause implements ClauseStatementType {

    @Override
    public Statement parse(TokenLine tokens, Parser parser) {
        if (tokens.size() > 1) {
            throw ErrorLog.raise(BAD_IF, "Unexpected symbol '%s' after 'else' " +
                    "(did you mean 'elsif'?)", tokens.get(1));
        }
        return new Statement(this);
    }

    @Override
    public String getKeyword() {
        return "else";
    }

}
