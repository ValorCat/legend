package compiletime.statement.block;

import compiletime.Parser;
import compiletime.TokenLine;
import compiletime.error.ErrorLog;
import compiletime.statement.Statement;
import compiletime.statement.block.clause.Clause;
import runtime.instruction.Instruction;
import runtime.instruction.JumpInstruction;

import java.util.List;

/**
 * @since 1/19/2019
 */
public class RepeatLoop implements BlockStatementType {

    @Override
    public Statement parseHeader(TokenLine tokens, Parser parser) {
        if (tokens.size() != 1) {
            throw ErrorLog.get("Unexpected symbol '%s' after 'repeat'", tokens.get(1));
        }
        return new Statement(this);
    }

    @Override
    public List<Instruction> build(Clause base, List<Clause> optional) {
        List<Instruction> body = base.BODY;
        return asList(body.size() + 1,
                body,
                new JumpInstruction(-body.size()));
    }

    @Override
    public String getName() {
        return "repeat";
    }

}
