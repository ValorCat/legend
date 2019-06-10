package runtime.instruction;

import runtime.Scope;

public interface Instruction {

    void execute(Scope scope);

}
