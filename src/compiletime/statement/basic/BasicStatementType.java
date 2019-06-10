package compiletime.statement.basic;

import compiletime.Compiler;
import compiletime.statement.Statement;
import compiletime.statement.StatementType;
import runtime.instruction.Instruction;

import java.util.List;

public interface BasicStatementType extends StatementType {

    List<Instruction> build(Statement data);

    @Override
    default List<Instruction> compile(Statement stmt, Compiler compiler) {
        return build(stmt);
    }

}
