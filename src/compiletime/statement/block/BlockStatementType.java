package compiletime.statement.block;

import compiletime.Compiler;
import compiletime.Parser;
import compiletime.TokenLine;
import compiletime.statement.Statement;
import compiletime.statement.StatementType;
import compiletime.statement.block.clause.ClauseData;
import runtime.instruction.Instruction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface BlockStatementType extends StatementType {

    Statement parseHeader(TokenLine tokens, Parser parser);
    List<Instruction> build(List<ClauseData> clauses);

    default boolean allowsClause(String keyword) {
        return false;
    }

    @Override
    default Statement parse(TokenLine tokens, Parser parser) {
        parser.getControlStack().push(this);
        return parseHeader(tokens, parser);
    }

    @Override
    default List<Instruction> compile(Statement stmt, Compiler compiler) {
        return compiler.compileBlockStatement(stmt);
    }

    @SuppressWarnings("unchecked")
    default List<Instruction> asList(int length, Object... instructions) {
        List<Instruction> result = new ArrayList<>(length);
        for (Object instr : instructions) {
            if (instr instanceof Instruction) {
                result.add((Instruction) instr);
            } else if (instr instanceof List) {
                result.addAll((Collection<? extends Instruction>) instr);
            }
        }
        return result;
    }


}
