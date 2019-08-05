package legend.compiletime.statement.block.clause;

import legend.compiletime.Parser;
import legend.compiletime.TokenLine;
import legend.compiletime.error.ErrorLog;
import legend.compiletime.statement.Statement;

public class ElseClause implements ClauseStatement {

    @Override
    public Statement parse(TokenLine tokens, Parser parser) {
        if (tokens.size() > 1) {
            throw ErrorLog.get("Unexpected symbol '%s' after 'else' (did you mean 'else if'?)", tokens.get(1));
        }
        return new ElseClause();
    }

    @Override
    public String getName() {
        return "else";
    }

}
