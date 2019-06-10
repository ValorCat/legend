package statement.basic;

import instruction.Instruction;
import parse.Parser;
import parse.TokenLine;
import parse.error.ErrorLog;
import statement.StatementData;

import java.util.List;

import static parse.error.ErrorDescription.BAD_NESTING;

/**
 * @since 1/19/2019
 */
public class EndStatement implements BasicStatementType {

    @Override
    public boolean matches(TokenLine tokens) {
        return tokens.get(0).matches("end");
    }

    @Override
    public StatementData parse(TokenLine tokens, Parser parser) {
        if (tokens.size() != 1) {
            throw ErrorLog.raise(BAD_NESTING, "Unexpected symbol '%s' after 'end'", tokens.get(1));
        } else if (parser.getControlStack().isEmpty()) {
            throw ErrorLog.raise(BAD_NESTING, "Mismatched 'end'");
        }
        parser.getControlStack().pop();
        return new StatementData(this);
    }

    @Override
    public List<Instruction> build(StatementData data) {
        return List.of();
    }

    @Override
    public String getKeyword() {
        return "end";
    }
}
