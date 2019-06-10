package statement.block;

import instruction.Instruction;
import instruction.JumpInstruction;
import instruction.JumpUnlessInstruction;
import parse.Parser;
import parse.TokenLine;
import parse.error.ErrorLog;
import statement.StatementData;
import statement.block.clause.ClauseData;

import java.util.ArrayList;
import java.util.List;

import static parse.error.ErrorDescription.BAD_IF;

/**
 * @since 1/27/2019
 */
public class IfStatement implements BlockStatementType {

    @Override
    public StatementData parseHeader(TokenLine tokens, Parser parser) {
        if (tokens.size() == 1) {
            throw ErrorLog.raise(BAD_IF, "Expected expression after 'if'");
        }
        return new StatementData(this, parser.parseFrom(tokens, 1));
    }

    @Override
    public List<Instruction> build(List<ClauseData> clauses) {
        // todo assert that if there is an 'else' clause, it occurs only once and at the end
        int remaining = computeCompiledSize(clauses);
        List<Instruction> compiled = new ArrayList<>(remaining);
        for (ClauseData clause : clauses) {
            remaining -= clause.BODY.size() + 2;
            if (clause.TYPE.equals("else")) {
                compiled.addAll(clause.BODY);
            } else {
                int offset = remaining > 0 ? 2 : 1;
                compiled.add(new JumpUnlessInstruction(clause.BODY.size() + offset, clause.HEADER.EXPRESSION));
                compiled.addAll(clause.BODY);
                if (remaining > 0) {
                    compiled.add(new JumpInstruction(remaining));
                }
            }
        }
        return compiled;
    }

    @Override
    public boolean allowsClause(String keyword) {
        return keyword.equals("else") || keyword.equals("elsif");
    }

    @Override
    public String getKeyword() {
        return "if";
    }

    private static int computeCompiledSize(List<ClauseData> clauses) {
        int size = 0;
        for (ClauseData clause : clauses) {
            size += clause.BODY.size() + 2;
        }
        if (clauses.get(clauses.size() - 1).TYPE.equals("else")) {
            size -= 1;
        }
        return size;
    }

}
