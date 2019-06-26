package legend.runtime.instruction;

import legend.compiletime.expression.Expression;
import legend.runtime.Program;
import legend.runtime.Scope;

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
