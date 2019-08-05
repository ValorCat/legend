package legend.runtime.instruction;

import legend.compiletime.statement.Statement;
import legend.runtime.Program;
import legend.runtime.Scope;

/**
 * An assembly-like instruction to be executed by {@link Program#execute}. Instructions are generated at compile-time by
 * {@link legend.compiletime.Compiler}. The instruction translations for each statement and control structure are defined by
 * the implementations of {@link Statement} in the {@link legend.compiletime.statement} package.
 * @see legend.runtime.Program
 * @see legend.compiletime.Compiler
 */
public interface Instruction {

    /**
     * Execute this instruction.
     * @param scope the current scope
     * @see Scope
     */
    void execute(Scope scope);

}
