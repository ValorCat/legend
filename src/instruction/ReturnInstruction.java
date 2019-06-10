package instruction;

import execute.Scope;
import expression.Expression;

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
