package runtime.instruction;

import compiletime.expression.group.Parentheses;
import compiletime.expression.value.function.UserDefinedFunction;
import runtime.Program;
import runtime.Scope;

public class DefineFunctionInstruction implements Instruction {

    private String name;
    private Parentheses params;

    public DefineFunctionInstruction(String name, Parentheses params) {
        this.name = name;
        this.params = params;
    }

    @Override
    public void execute(Scope scope) {
        int startAddress = Program.PROGRAM.getCounter() + 2;
        UserDefinedFunction function = new UserDefinedFunction(name, params, scope, startAddress);
        scope.setVariable(name, function);
    }

    @Override
    public String toString() {
        return String.format("def %s %s", name, params);
    }

}
