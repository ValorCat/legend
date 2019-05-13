package instruction;

import execute.Environment;

public class PopStackInstruction implements Instruction {

    @Override
    public void execute(Environment env) {
        env.getStack().pop();
    }

}
