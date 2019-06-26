package legend.runtime.instruction;

import legend.compiletime.expression.Expression;
import legend.runtime.Scope;

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

    @Override
    public String toString() {
        return String.format("asn %s %s", target, value);
    }

}
