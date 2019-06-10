package runtime.instruction;

import runtime.Program;
import runtime.Scope;

public class PopStackInstruction implements Instruction {

    @Override
    public void execute(Scope scope) {
        Program.PROGRAM.getStack().pop();
    }

    @Override
    public String toString() {
        return "pop";
    }

}
