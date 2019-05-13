package instruction;

import execute.Environment;
import expression.Expression;

public class AssignInstruction implements Instruction {

    private String target;
    private Expression value;

    public AssignInstruction(String target, Expression value) {
        this.target = target;
        this.value = value;
    }

    @Override
    public void execute(Environment env) {
        env.assignLocal(target, value.evaluate(env));
    }

}
