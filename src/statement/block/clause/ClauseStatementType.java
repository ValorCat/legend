package statement.block.clause;

import instruction.Instruction;
import parse.Parser;
import statement.StatementData;
import statement.StatementType;

import java.util.List;

public interface ClauseStatementType extends StatementType {

    @Override
    default List<Instruction> compile(StatementData data, Parser parser) {
        // should never be reached
        throw new UnsupportedOperationException("Cannot compile clause statement");
    }

}
