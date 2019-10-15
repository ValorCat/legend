package legend.compiletime.expression.value;

import legend.compiletime.expression.group.ArgumentList;
import legend.compiletime.expression.group.ParameterList;
import legend.runtime.Program;
import legend.runtime.Scope;
import legend.runtime.type.Type;

/**
 * @since 2/16/2019
 */
public class UserDefinedFunction extends FunctionValue {

    private Scope parentScope;
    private ParameterList params;
    private Type returnType;
    private int beginAddress;

    public UserDefinedFunction(String name, ParameterList params, Type returnType, Scope parentScope, int beginAddress) {
        super(name);
        this.parentScope = parentScope;
        this.params = params;
        this.returnType = returnType;
        this.beginAddress = beginAddress;
    }

    @Override
    public Value call(ArgumentList args) {
        Scope newScope = new Scope(parentScope);
        params.accept(args, newScope);
        int returnAddress = Program.PROGRAM.getCounter();
        Value returnValue = Program.PROGRAM.executeSubroutine(beginAddress, newScope);
        if (!returnType.isSupertypeOf(returnValue.type())) {
            throw new RuntimeException(String.format("Cannot return %s value '%s' from %s function",
                    returnValue.type().getName(), returnValue.asString(), returnType.getName()));
        }
        Program.PROGRAM.setCounter(returnAddress + 1);
        return returnValue;
    }

    @Override
    public boolean equals(Value other) {
        return other instanceof UserDefinedFunction && beginAddress == ((UserDefinedFunction) other).beginAddress;
    }

    @Override
    public String toString() {
        return "function[" + getName() + "]";
    }

}
