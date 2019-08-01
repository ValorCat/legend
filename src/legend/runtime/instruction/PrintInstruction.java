package legend.runtime.instruction;

import legend.compiletime.expression.Expression;
import legend.runtime.Scope;

public class PrintInstruction implements Instruction {

    private String value;
    private Expression valueTree;

    public PrintInstruction(String value) {
        this.value = value;
    }

    public PrintInstruction(Expression valueTree) {
        this.valueTree = valueTree;
    }

    @Override
    public void execute(Scope scope) {
        System.out.println(value != null ? value : valueTree.evaluate(scope).asString());
    }

    @Override
    public String toString() {
        return String.format("prt \"%s\"", value);
    }

}
