package compiletime.expression.value.function;

import compiletime.expression.group.ArgumentList;
import compiletime.expression.group.ParameterList;
import compiletime.expression.group.Parentheses;
import compiletime.expression.value.Value;
import runtime.Program;
import runtime.Scope;

/**
 * @since 2/16/2019
 */
public class UserDefinedFunction extends LFunction {

    private Scope parentScope;
    private ParameterList params;
    private int beginAddress;

    public UserDefinedFunction(String name, Parentheses params, Scope parentScope, int beginAddress) {
        super(name);
        this.parentScope = parentScope;
        this.params = new ParameterList(name, params.getContents());
        this.beginAddress = beginAddress;
    }

    @Override
    public Value call(ArgumentList args, Scope scope) {
        Scope newScope = new Scope(parentScope);
        params.accept(args, parentScope, newScope);
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