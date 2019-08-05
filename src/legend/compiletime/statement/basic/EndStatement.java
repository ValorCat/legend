package legend.compiletime.statement.basic;

import legend.compiletime.Parser;
import legend.compiletime.TokenLine;
import legend.compiletime.error.ErrorLog;
import legend.compiletime.statement.Statement;
import legend.runtime.instruction.Instruction;

import java.util.List;

/**
 * @since 1/19/2019
 */
public class EndStatement implements BasicStatement {

    @Override
    public Statement parse(TokenLine tokens, Parser parser) {
        if (tokens.size() != 1) {
            throw ErrorLog.get("Unexpected symbol '%s' after 'end'", tokens.get(1));
        } else if (parser.getControlStack().isEmpty()) {
            throw ErrorLog.get("Mismatched 'end'");
        }
        parser.getControlStack().pop();
        return new EndStatement();
    }

    @Override
    public List<Instruction> build() {
        return List.of();
    }

    @Override
    public String getName() {
        return "end";
    }

}
