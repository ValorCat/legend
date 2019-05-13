package instruction;

import execute.Environment;

public class PrintInstruction implements Instruction {

    private String value;

    public PrintInstruction(String value) {
        this.value = value;
    }

    @Override
    public void execute(Environment env) {
        System.out.println(value);
    }

}
