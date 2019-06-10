package statement.basic;

import instruction.Instruction;
import parse.Parser;
import statement.Statement;
import statement.StatementData;

import java.util.List;

public interface BasicStatement extends Statement {

    List<Instruction> build(StatementData data);

    @Override
    default List<Instruction> compile(StatementData data, int nestingDepth, Parser parser) {
        return build(data);
    }

}
