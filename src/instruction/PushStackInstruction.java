package instruction;

import execute.Environment;
import expression.Expression;

public class PushStackInstruction implements Instruction {

    private Expression value;

    public PushStackInstruction(Expression value) {
        this.value = value;
    }

    @Override
    public void execute(Environment env) {
        env.getStack().push(value.evaluate(env));
    }

}
