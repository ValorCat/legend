package expression;

import execute.Program;
import execute.Scope;
import expression.value.Value;

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
