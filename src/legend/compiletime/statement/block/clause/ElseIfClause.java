package legend.compiletime.statement.block.clause;

import legend.compiletime.Parser;
import legend.compiletime.TokenLine;
import legend.compiletime.error.ErrorLog;
import legend.compiletime.statement.Statement;

public class ElseIfClause implements ClauseStatementType {

    @Override
    public boolean matches(TokenLine tokens) {
        return tokens.size() > 1
                && tokens.get(0).matches("else")
                && tokens.get(1).matches("if");
    }

    @Override
    public Statement parse(TokenLine tokens, Parser parser) {
        if (tokens.size() == 2) {
            throw ErrorLog.get("Expected boolean expression after 'else if' (did you mean 'else'?)");
        }
        return new Statement(this, parser.parseFrom(tokens, 2));
    }

    @Override
    public String getName() {
        return "else if";
    }

}
