package compiletime.statement.basic;

import compiletime.Parser;
import compiletime.TokenLine;
import compiletime.error.ErrorLog;
import compiletime.statement.Statement;
import runtime.instruction.Instruction;

import java.util.List;

/**
 * @since 1/19/2019
 */
public class EndStatement implements BasicStatementType {

    @Override
    public Statement parse(TokenLine tokens, Parser parser) {
        if (tokens.size() != 1) {
            throw ErrorLog.get("Unexpected symbol '%s' after 'end'", tokens.get(1));
        } else if (parser.getControlStack().isEmpty()) {
            throw ErrorLog.get("Mismatched 'end'");
        }
        parser.getControlStack().pop();
        return new Statement(this);
    }

    @Override
    public List<Instruction> build(Statement stmt) {
        return List.of();
    }

    @Override
    public String getName() {
        return "end";
    }
}
