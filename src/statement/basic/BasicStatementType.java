package statement.basic;

import instruction.Instruction;
import parse.Compiler;
import statement.Statement;
import statement.StatementType;

import java.util.List;

public interface BasicStatementType extends StatementType {

    List<Instruction> build(Statement data);

    @Override
    default List<Instruction> compile(Statement stmt, Compiler compiler) {
        return build(stmt);
    }

}
