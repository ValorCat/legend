package compiletime.statement.basic;

import compiletime.Parser;
import compiletime.TokenLine;
import compiletime.error.ErrorLog;
import compiletime.statement.Statement;
import runtime.instruction.Instruction;

import java.util.List;

import static compiletime.error.ErrorDescription.BAD_NESTING;

/**
 * @since 1/19/2019
 */
public class EndStatement implements BasicStatementType {

    @Override
    public boolean matches(TokenLine tokens) {
        return tokens.get(0).matches("end");
    }

    @Override
    public Statement parse(TokenLine tokens, Parser parser) {
        if (tokens.size() != 1) {
            throw ErrorLog.raise(BAD_NESTING, "Unexpected symbol '%s' after 'end'", tokens.get(1));
        } else if (parser.getControlStack().isEmpty()) {
            throw ErrorLog.raise(BAD_NESTING, "Mismatched 'end'");
        }
        parser.getControlStack().pop();
        return new Statement(this);
    }

    @Override
    public List<Instruction> build(Statement data) {
        return List.of();
    }

    @Override
    public String getKeyword() {
        return "end";
    }
}
