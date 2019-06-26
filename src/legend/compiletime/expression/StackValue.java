package legend.compiletime.expression;

import legend.compiletime.expression.value.Value;
import legend.runtime.Program;
import legend.runtime.Scope;

/**
 * This class provides a means of accessing the runtime stack from within an expression. Calling the {@link #evaluate}
 * method returns the value at the top of the stack without modifying it. Unlike most expression types, this class
 * cannot be created directly by the user. To add a value to the stack, see {@link legend.runtime.instruction.PushStackInstruction}.
 * @see Program#getStack
 * @see legend.runtime.instruction.PushStackInstruction
 * @see legend.runtime.instruction.PopStackInstruction
 * @since 5/28/2019
 */
public class StackValue implements Expression {

    @Override
    public Value evaluate(Scope scope) {
        return Program.PROGRAM.getStack().peek();
    }

    @Override
    public String toString() {
        return "<stack>";
    }

}
