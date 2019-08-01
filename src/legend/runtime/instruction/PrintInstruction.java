package legend.runtime.instruction;

import legend.compiletime.expression.Expression;
import legend.runtime.Scope;

public class PrintInstruction implements Instruction {

    private Expression value;

    public PrintInstruction(Expression value) {
        this.value = value;
    }

    @Override
    public void execute(Scope scope) {
        System.out.println(value.evaluate(scope).asString());
    }

}
