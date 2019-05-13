package instruction;

import execute.Environment;

public class GotoInstruction implements Instruction {

    private int label;

    public GotoInstruction(int label) {
        this.label = label;
    }

    @Override
    public void execute(Environment env) {
        env.setCounter(env.getLabelAddress(label));
    }

}
