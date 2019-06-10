package compiletime.statement.block.clause;

import compiletime.statement.Statement;
import runtime.instruction.Instruction;

import java.util.List;

public class ClauseData {

    public final String TYPE;
    public final Statement HEADER;
    public final List<Instruction> BODY;

    public ClauseData(String type, Statement header, List<Instruction> body) {
        this.TYPE = type;
        this.HEADER = header;
        this.BODY = body;
    }

}
