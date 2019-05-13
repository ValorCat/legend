package instruction;

import execute.Environment;
import expression.Expression;

public class ReturnInstruction implements Instruction {

    private Expression value;

    public ReturnInstruction(Expression value) {
        this.value = value;
    }

    @Override
    public void execute(Environment env) {
        if (value != null) {
            env.setReturnValue(value.evaluate(env));
        }
        env.setCounter(env.getReturnStack().pop());
    }

}
