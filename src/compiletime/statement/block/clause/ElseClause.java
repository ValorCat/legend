package compiletime.statement.block.clause;

import compiletime.Parser;
import compiletime.TokenLine;
import compiletime.error.ErrorLog;
import compiletime.statement.Statement;

import static compiletime.error.ErrorDescription.BAD_IF;

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
    public String getName() {
        return "else";
    }

}
