package statement.basic;

import instruction.Instruction;
import statement.Statement;
import statement.StatementData;

import java.util.List;

public interface BasicStatement extends Statement {

    List<Instruction> compile(StatementData data);

}
