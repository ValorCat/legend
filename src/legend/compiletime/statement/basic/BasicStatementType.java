package legend.compiletime.statement.basic;

import legend.compiletime.Compiler;
import legend.compiletime.statement.Statement;
import legend.compiletime.statement.StatementType;
import legend.runtime.instruction.Instruction;

import java.util.List;

/**
 * Represents a simple statement type, like assignment or returning.
 * @see legend.compiletime.statement.basic
 */
public interface BasicStatementType extends StatementType {

    /**
     * Generate the runtime instructions for a statement.
     * @param stmt the statement data
     * @return the corresponding instructions
     */
    List<Instruction> build(Statement stmt);

    @Override
    default List<Instruction> compile(Statement stmt, Compiler compiler) {
        return build(stmt);
    }

}
