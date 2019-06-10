package instruction;

import execute.Program;
import execute.Scope;
import expression.Expression;

public class PushStackInstruction implements Instruction {

    private Expression value;

    public PushStackInstruction(Expression value) {
        this.value = value;
    }

    @Override
    public void execute(Scope scope) {
        Program.PROGRAM.getStack().push(value.evaluate(scope));
    }

    @Override
    public String toString() {
        return String.format("psh %s", value);
    }

}
