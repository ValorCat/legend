package dataformat.flow;

import execute.Environment;

/**
 * @since 12/30/2018
 */
public class RepeatControl implements FlowController {

    private int startIndex;

    public RepeatControl(Environment env) {
        this.startIndex = env.getCounter();
    }

    @Override
    public void onBegin(Environment env) {
    }

    @Override
    public boolean onEnd(Environment env) {
        env.setCounter(startIndex);
        return false;
    }

}
