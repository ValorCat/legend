package instruction;

import execute.Program;
import execute.Scope;

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
