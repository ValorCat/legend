package dataformat.operation.flow;

import dataformat.operation.Operation;
import execute.Environment;
import parse.Token;

import java.util.List;

/**
 * Classes that implement this interface represent flow control structures,
 * like if, while, etc. Instances of these classes are maintained in a stack
 * in the environment.
 * @since 12/29/2018
 */
public interface FlowController {

    /**
     * Check if this flow control structure is done and should be popped
     * from the stack. This is called when an 'end' statement is reached
     * during execution.
     * @param env the environment
     * @return whether this structure is done
     */
    boolean isDone(Environment env);

    /**
     * Add a new jump destination to this flow control structure. A jump
     * point is an instruction address that may be jumped to during the
     * execution of this structure. Examples include the 'end' statement
     * of a loop and 'elsif' clauses in an 'if' statement. This method
     * is called during parsing.
     * @param address the instruction address to allow jumping to
     * @param tokenPos the position of the keyword within its statement
     *                 that marked this instruction as a jump point
     * @param statement the statement to jump to
     */
    void setJumpPoint(int address, int tokenPos, List<Token> statement);

    /**
     * Get the keyword associated with this flow control structure, such
     * as 'if' or 'for'. This is displayed in appropriate error messages.
     * @return the keyword associated with this structure
     */
    default String getKeyword() {
        return ((Operation) this).getOperator();
    }

}
