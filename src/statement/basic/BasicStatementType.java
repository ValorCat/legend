package statement.basic;

import instruction.Instruction;
import parse.Parser;
import statement.Statement;
import statement.StatementType;

import java.util.List;

public interface BasicStatementType extends StatementType {

    List<Instruction> build(Statement data);

    @Override
    default List<Instruction> compile(Statement data, Parser parser) {
        return build(data);
    }

}
