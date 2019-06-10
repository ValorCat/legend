package statement.block.clause;

import instruction.Instruction;
import statement.StatementData;

import java.util.List;

public class ClauseData {

    public final String TYPE;
    public final StatementData DATA;
    public final List<Instruction> BODY;

    public ClauseData(String type, StatementData data, List<Instruction> body) {
        this.TYPE = type;
        this.DATA = data;
        this.BODY = body;
    }

}
