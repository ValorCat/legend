package legend.runtime.instruction;

import legend.runtime.Program;
import legend.runtime.Scope;

public class JumpInstruction implements Instruction {

    private int offset;

    public JumpInstruction(int offset) {
        this.offset = offset;
    }

    @Override
    public void execute(Scope scope) {
        Program.PROGRAM.offsetCounter(offset);
    }

    @Override
    public String toString() {
        return String.format("jmp %d", offset);
    }

}
