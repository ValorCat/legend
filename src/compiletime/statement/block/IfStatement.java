package compiletime.statement.block;

import compiletime.Parser;
import compiletime.TokenLine;
import compiletime.error.ErrorLog;
import compiletime.statement.Statement;
import compiletime.statement.block.clause.Clause;
import runtime.instruction.Instruction;
import runtime.instruction.JumpInstruction;
import runtime.instruction.JumpUnlessInstruction;

import java.util.ArrayList;
import java.util.List;

import static compiletime.error.ErrorDescription.BAD_IF;

/**
 * @since 1/27/2019
 */
public class IfStatement implements BlockStatementType {

    @Override
    public Statement parseHeader(TokenLine tokens, Parser parser) {
        if (tokens.size() == 1) {
            throw ErrorLog.raise(BAD_IF, "Expected expression after 'if'");
        }
        return new Statement(this, parser.parseFrom(tokens, 1));
    }

    @Override
    public List<Instruction> build(List<Clause> clauses) {
        // todo assert that if there is an 'else' clause, it occurs only once and at the end
        int remaining = computeCompiledSize(clauses);
        List<Instruction> compiled = new ArrayList<>(remaining);
        for (Clause clause : clauses) {
            remaining -= clause.BODY.size() + 2;
            if (clause.NAME.equals("else")) {
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
    public boolean allowsClause(String name) {
        return name.equals("else") || name.equals("elsif");
    }

    @Override
    public String getName() {
        return "if";
    }

    private static int computeCompiledSize(List<Clause> clauses) {
        int size = 0;
        for (Clause clause : clauses) {
            size += clause.BODY.size() + 2;
        }
        if (clauses.get(clauses.size() - 1).NAME.equals("else")) {
            size -= 1;
        }
        return size;
    }

}
