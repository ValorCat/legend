package compiletime.statement.block.clause;

import compiletime.statement.Statement;
import runtime.instruction.Instruction;

import java.util.List;

public class Clause {

    public final String NAME;
    public final Statement HEADER;
    public final List<Instruction> BODY;

    public Clause(Statement header, List<Instruction> body) {
        this.NAME = header.TYPE.getName();
        this.HEADER = header;
        this.BODY = body;
    }

}
