package statement.block.clause;

import instruction.Instruction;
import statement.StatementData;

import java.util.List;

public class ClauseData {

    public final String TYPE;
    public final StatementData HEADER;
    public final List<Instruction> BODY;

    public ClauseData(String type, StatementData header, List<Instruction> body) {
        this.TYPE = type;
        this.HEADER = header;
        this.BODY = body;
    }

}
