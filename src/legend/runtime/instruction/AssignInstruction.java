package legend.runtime.instruction;

import legend.compiletime.expression.Expression;
import legend.runtime.Scope;

public class AssignInstruction implements Instruction {

    private String target;
    private Expression type;
    private Expression value;

    public AssignInstruction(String target, Expression type, Expression value) {
        this.target = target;
        this.type = type;
        this.value = value;
    }

    @Override
    public void execute(Scope scope) {
        scope.setVariable(target, type.evaluate(scope).asType(), value.evaluate(scope));
    }

    @Override
    public String toString() {
        return String.format("asn %s %s %s", target, type, value);
    }

}
