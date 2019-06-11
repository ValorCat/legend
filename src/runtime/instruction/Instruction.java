package runtime.instruction;

import runtime.Program;
import runtime.Scope;

/**
 * An assembly-like instruction to be executed by {@link Program#execute}. Instructions are generated at compile-time by
 * {@link compiletime.Compiler}. The instruction translations for each statement and control structure are defined by
 * the implementations of {@link compiletime.statement.StatementType} in the {@link compiletime.statement} package.
 * @see runtime.Program
 * @see compiletime.Compiler
 */
public interface Instruction {

    /**
     * Execute this instruction.
     * @param scope the current scope
     * @see Scope
     */
    void execute(Scope scope);

}
