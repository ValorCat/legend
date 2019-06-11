package compiletime.statement.block.clause;

import compiletime.statement.Statement;
import runtime.instruction.Instruction;

import java.util.List;

/**
 * Represents a clause within a multi-line statement, such as an 'elsif' clause within an if statement. This class
 * stores the clause's name, header data, and compiled body.
 * @see ClauseStatementType
 */
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
