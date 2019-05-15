package instruction;

import execute.Scope;
import expression.Expression;

public class AssignInstruction implements Instruction {

    private String target;
    private Expression value;

    public AssignInstruction(String target, Expression value) {
        this.target = target;
        this.value = value;
    }

    @Override
    public void execute(Scope scope) {
        scope.setVariable(target, value.evaluate(scope));
    }

}
