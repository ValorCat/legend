package legend.runtime.instruction;

import legend.compiletime.expression.Expression;
import legend.runtime.Scope;

public class ReturnInstruction implements Instruction {

    private Expression value;

    public ReturnInstruction() {}

    public ReturnInstruction(Expression value) {
        this.value = value;
    }

    @Override
    public void execute(Scope scope) {
        if (value != null) {
            scope.setReturnValue(value.evaluate(scope));
        }
        scope.setReturnFlag();
    }

    @Override
    public String toString() {
        return String.format("ret %s", value);
    }

}
