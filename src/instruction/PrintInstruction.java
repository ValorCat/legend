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

    @Override
    public String toString() {
        return String.format("prt \"%s\"", value);
    }

}
