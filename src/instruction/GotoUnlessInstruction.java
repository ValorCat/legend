package instruction;

import execute.Environment;
import expression.Expression;

public class GotoUnlessInstruction implements Instruction {

    private int label;
    private Expression condition;

    public GotoUnlessInstruction(int label, Expression condition) {
        this.label = label;
        this.condition = condition;
    }

    @Override
    public void execute(Environment env) {
        if (!condition.evaluate(env).asBoolean()) {
            env.setCounter(env.getLabelAddress(label));
        }
    }

}
