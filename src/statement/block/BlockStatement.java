package statement.block;

import instruction.Instruction;
import parse.Parser;
import parse.TokenLine;
import statement.Statement;
import statement.StatementData;
import statement.block.clause.ClauseData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface BlockStatement extends Statement {

    @Override
    default StatementData parse(TokenLine tokens, Parser parser) {
        parser.getControlStack().push(this);
        return parseHeader(tokens, parser);
    }

    StatementData parseHeader(TokenLine tokens, Parser parser);
    List<Instruction> compile(List<ClauseData> clauses);

    default boolean allowsClause(String keyword) {
        return false;
    }

    @SuppressWarnings("unchecked")
    default List<Instruction> build(int length, Object... instructions) {
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
