package compiletime.statement.block.clause;

import compiletime.Compiler;
import compiletime.statement.Statement;
import compiletime.statement.StatementType;
import runtime.instruction.Instruction;

import java.util.List;

public interface ClauseStatementType extends StatementType {

    @Override
    default List<Instruction> compile(Statement stmt, Compiler compiler) {
        // should never be reached
        throw new UnsupportedOperationException("Cannot compile clause statement");
    }

}
