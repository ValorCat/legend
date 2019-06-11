package compiletime.statement.block.clause;

import compiletime.Parser;
import compiletime.TokenLine;
import compiletime.error.ErrorLog;
import compiletime.statement.Statement;

import static compiletime.error.ErrorDescription.BAD_IF;

public class ElsifClause implements ClauseStatementType {

    @Override
    public Statement parse(TokenLine tokens, Parser parser) {
        if (tokens.size() == 1 || !tokens.get(1).isValue()) {
            throw ErrorLog.raise(BAD_IF, "Expected boolean expression after 'elsif' (did you mean 'else'?)");
        }
        return new Statement(this, parser.parseFrom(tokens, 1));
    }

    @Override
    public String getName() {
        return "elsif";
    }

}