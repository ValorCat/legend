package legend.compiletime.statement.basic;

import legend.compiletime.Parser;
import legend.compiletime.TokenLine;
import legend.compiletime.statement.Statement;
import legend.runtime.instruction.Instruction;
import legend.runtime.instruction.ReturnInstruction;

import java.util.List;

/**
 * @since 2/16/2019
 */
public class ReturnStatement implements BasicStatementType {

    @Override
    public Statement parse(TokenLine tokens, Parser parser) {
        if (tokens.size() == 1) {
            return new Statement(this);
        } else {
            return new Statement(this, parser.parseFrom(tokens, 1));
        }
    }

    @Override
    public List<Instruction> build(Statement stmt) {
        return List.of(new ReturnInstruction(stmt.EXPRESSION));
    }

    @Override
    public String getName() {
        return "return";
    }
}
