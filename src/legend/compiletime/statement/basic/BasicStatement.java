package legend.compiletime.statement.basic;

import legend.compiletime.Compiler;
import legend.compiletime.statement.Statement;
import legend.runtime.instruction.Instruction;

import java.util.List;

/**
 * Represents a simple statement type, like assignment or returning.
 * @see legend.compiletime.statement.basic
 */
public interface BasicStatement extends Statement {

    /**
     * Generate the runtime instructions for a statement.
     * @return the corresponding instructions
     */
    List<Instruction> build();

    @Override
    default List<Instruction> compile(Compiler compiler) {
        return build();
    }

}
