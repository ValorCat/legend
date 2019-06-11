package compiletime.statement.basic;

import compiletime.Compiler;
import compiletime.statement.Statement;
import compiletime.statement.StatementType;
import runtime.instruction.Instruction;

import java.util.List;

/**
 * Represents a simple statement type, like assignment or returning.
 * @see compiletime.statement.basic
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
