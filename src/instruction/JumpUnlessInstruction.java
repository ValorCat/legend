package instruction;

import execute.Program;
import execute.Scope;
import expression.Expression;

public class JumpUnlessInstruction implements Instruction {

    private int offset;
    private Expression condition;

    public JumpUnlessInstruction(int offset, Expression condition) {
        this.offset = offset;
        this.condition = condition;
    }

    @Override
    public void execute(Scope scope) {
        if (!condition.evaluate(scope).asBoolean()) {
            Program.PROGRAM.offsetCounter(offset);
        }
    }

    @Override
    public String toString() {
        return String.format("jun %d %s", offset, condition);
    }

}
