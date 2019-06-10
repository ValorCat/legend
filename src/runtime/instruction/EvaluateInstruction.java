package runtime.instruction;

import compiletime.expression.Expression;
import runtime.Scope;

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
