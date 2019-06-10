package statement.block.clause;

import instruction.Instruction;
import parse.Parser;
import statement.Statement;
import statement.StatementData;

import java.util.List;

public interface BlockClauseStatement extends Statement {

    @Override
    default List<Instruction> compile(StatementData data, int nestingDepth, Parser parser) {
        // should never be reached
        throw new UnsupportedOperationException("Cannot compile clause statement");
    }

}
