package statement.block.clause;

import parse.Parser;
import parse.TokenLine;
import parse.error.ErrorLog;
import statement.StatementData;

import static parse.error.ErrorDescription.BAD_IF;

public class ElsifClause implements ClauseStatementType {

    @Override
    public StatementData parse(TokenLine tokens, Parser parser) {
        if (tokens.size() == 1 || !tokens.get(1).isValue()) {
            throw ErrorLog.raise(BAD_IF, "Expected boolean expression after 'elsif' (did you mean 'else'?)");
        }
        return new StatementData(this, parser.parseFrom(tokens, 1));
    }

    @Override
    public String getKeyword() {
        return "elsif";
    }

}
