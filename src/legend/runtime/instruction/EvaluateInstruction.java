package legend.runtime.instruction;

import legend.compiletime.expression.Expression;
import legend.runtime.Scope;

public class EvaluateInstruction implements Instruction {

    private Expression value;

    public EvaluateInstruction(Expression value) {
        this.value = value;
    }

    @Override
    public void execute(Scope scope) {
        value.evaluate(scope);
    }

    @Override
    public String toString() {
        return String.format("evl %s", value);
    }

}
