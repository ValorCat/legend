package instruction;

import execute.Scope;

public class PrintInstruction implements Instruction {

    private String value;

    public PrintInstruction(String value) {
        this.value = value;
    }

    @Override
    public void execute(Scope scope) {
        System.out.println(value);
    }

}
