package legend.runtime.instruction;

import legend.compiletime.expression.group.ParameterList;
import legend.compiletime.expression.value.UserDefinedFunction;
import legend.runtime.Program;
import legend.runtime.Scope;

public class DefineFunctionInstruction implements Instruction {

    private String name;
    private ParameterList params;

    public DefineFunctionInstruction(String name, ParameterList params) {
        this.name = name;
        this.params = params;
    }

    @Override
    public void execute(Scope scope) {
        int startAddress = Program.PROGRAM.getCounter() + 2;
        params.resolveTypes(scope);
        UserDefinedFunction function = new UserDefinedFunction(name, params, scope, startAddress);
        scope.setLocalVariable(name, function);
    }

    @Override
    public String toString() {
        return String.format("def %s %s", name, params);
    }

}
