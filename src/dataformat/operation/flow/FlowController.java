package dataformat.operation.flow;

import execute.Environment;

/**
 * Classes that implement this interface represent flow control structures,
 * like if, while, etc. Instances of these classes are maintained in a stack
 * in the environment.
 * @since 12/29/2018
 */
public interface FlowController {

    boolean isDone(Environment env);
    void setEndIndex(int endIndex);

}
