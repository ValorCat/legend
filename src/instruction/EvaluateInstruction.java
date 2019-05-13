package instruction;

import execute.Environment;
import expression.Expression;

public class EvaluateInstruction implements Instruction {

    private Expression value;

    public EvaluateInstruction(Expression value) {
        this.value = value;
    }

    @Override
    public void execute(Environment env) {
        value.evaluate(env);
    }

}
