package statement.block;

import instruction.Instruction;
import instruction.JumpInstruction;
import parse.Parser;
import parse.TokenLine;
import parse.error.ErrorLog;
import statement.StatementData;
import statement.block.clause.ClauseData;

import java.util.List;

import static parse.error.ErrorDescription.BAD_REPEAT;

/**
 * @since 1/19/2019
 */
public class RepeatLoop implements BlockStatement {

    @Override
    public StatementData parseHeader(TokenLine tokens, Parser parser) {
        if (tokens.size() != 1) {
            throw ErrorLog.raise(BAD_REPEAT, "Unexpected symbol '%s' after 'repeat'", tokens.get(1));
        }
        return StatementData.EMPTY;
    }

    @Override
    public List<Instruction> compile(List<ClauseData> clauses) {
        List<Instruction> body = clauses.get(0).BODY;
        return build(body.size() + 1,
                body,
                new JumpInstruction(-body.size()));
    }

    @Override
    public String getKeyword() {
        return "repeat";
    }

}
