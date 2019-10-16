package legend.runtime.instruction;

import legend.Interpreter;
import legend.compiletime.expression.Expression;
import legend.compiletime.expression.group.ParameterList;
import legend.compiletime.expression.value.UserDefinedFunction;
import legend.runtime.Program;
import legend.runtime.Scope;
import legend.runtime.type.BuiltinType;
import legend.runtime.type.DynamicType;
import legend.runtime.type.Type;

public class DefineFunctionInstruction implements Instruction {

    private String name;
    private ParameterList params;
    private Expression returnType;

    public DefineFunctionInstruction(String name, ParameterList params, Expression returnType) {
        this.name = name;
        this.params = params;
        this.returnType = returnType;
    }

    @Override
    public void execute(Scope scope) {
        int startAddress = Program.PROGRAM.getCounter() + 2;
        params.resolveTypes(scope);
        UserDefinedFunction function = new UserDefinedFunction(name, params, resolveType(returnType, scope),
                scope, startAddress);
        if (Interpreter.strictTyping) {
            scope.setLocalVariable(name, BuiltinType.FUNCTION.get(), function);
        } else {
            scope.setLocalVariable(name, function);
        }
    }

    @Override
    public String toString() {
        return String.format("def %s %s", name, params);
    }

    private static Type resolveType(Expression type, Scope scope) {
        if (type == null) {
            return DynamicType.UNTYPED;
        }
        return type.evaluate(scope).asType();
    }

}
