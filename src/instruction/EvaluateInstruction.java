package instruction;

import execute.Scope;
import expression.Expression;

public class EvaluateInstruction implements Instruction {

    private Expression value;

    public EvaluateInstruction(Expression value) {
        this.value = value;
    }

    @Override
    public void execute(Scope scope) {
        value.evaluate(scope);
    }

}
