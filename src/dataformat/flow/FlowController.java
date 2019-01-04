package dataformat.flow;

import execute.Environment;

/**
 * @since 12/29/2018
 */
public interface FlowController {

    void onBegin(Environment env);
    boolean onEnd(Environment env);

}
