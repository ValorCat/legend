package instruction;

import execute.Program;
import execute.Scope;

public class JumpInstruction implements Instruction {

    private int offset;

    public JumpInstruction(int offset) {
        this.offset = offset;
    }

    @Override
    public void execute(Scope scope) {
        Program.PROGRAM.offsetCounter(offset);
    }

}
