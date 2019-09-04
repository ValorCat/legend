package legend.compiletime.expression.value;

import legend.compiletime.expression.group.ArgumentList;
import legend.compiletime.expression.group.ParameterList;
import legend.runtime.Program;
import legend.runtime.Scope;

/**
 * @since 2/16/2019
 */
public class UserDefinedFunction extends FunctionValue {

    private Scope parentScope;
    private ParameterList params;
    private int beginAddress;

    public UserDefinedFunction(String name, ParameterList params, Scope parentScope, int beginAddress) {
        super(name);
        this.parentScope = parentScope;
        this.params = params;
        this.beginAddress = beginAddress;
    }

    @Override
    public Value call(ArgumentList args) {
        Scope newScope = new Scope(parentScope);
        params.accept(args, newScope);
        int returnAddress = Program.PROGRAM.getCounter();
        Value returnValue = Program.PROGRAM.executeSubroutine(beginAddress, newScope);
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
