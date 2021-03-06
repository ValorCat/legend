package legend.compiletime.statement.block.clause;

import legend.compiletime.Compiler;
import legend.compiletime.statement.Statement;
import legend.compiletime.statement.StatementType;
import legend.runtime.instruction.Instruction;

import java.util.List;

/**
 * Represents a clause within a multi-line statement, such as an 'else' clause within an if statement. This interface is
 * mostly useful for instanceof checks.
 * @see legend.compiletime.statement.block.clause
 */
public interface ClauseStatementType extends StatementType {

    @Override
    default List<Instruction> compile(Statement stmt, Compiler compiler) {
        // should never be reached
        throw new UnsupportedOperationException("Cannot compile clause statement");
    }

}
