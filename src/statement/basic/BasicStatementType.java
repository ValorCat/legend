package statement.basic;

import instruction.Instruction;
import parse.Parser;
import statement.StatementData;
import statement.StatementType;

import java.util.List;

public interface BasicStatementType extends StatementType {

    List<Instruction> build(StatementData data);

    @Override
    default List<Instruction> compile(StatementData data, Parser parser) {
        return build(data);
    }

}
