package compiletime.expression;

import compiletime.expression.value.Value;
import runtime.Program;
import runtime.Scope;

/**
 * @since 5/28/2019
 */
public class StackValue implements Expression {

    @Override
    public Value evaluate(Scope scope) {
        return Program.PROGRAM.getStack().peek();
    }

    @Override
    public String toString() {
        return "<StackValue>";
    }

}
