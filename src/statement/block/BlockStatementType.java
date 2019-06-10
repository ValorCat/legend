package statement.block;

import instruction.Instruction;
import parse.Parser;
import parse.TokenLine;
import statement.Statement;
import statement.StatementType;
import statement.block.clause.ClauseData;

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
    default List<Instruction> compile(Statement data, Parser parser) {
        return parser.parseBlockStatement(this, data);
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
