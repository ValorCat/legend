package legend.compiletime.statement.block;

import legend.compiletime.Parser;
import legend.compiletime.TokenLine;
import legend.compiletime.error.ErrorLog;
import legend.compiletime.expression.Expression;
import legend.compiletime.statement.Statement;
import legend.compiletime.statement.block.clause.Clause;
import legend.compiletime.statement.block.clause.ElseIfClause;
import legend.runtime.instruction.Instruction;
import legend.runtime.instruction.JumpInstruction;
import legend.runtime.instruction.JumpUnlessInstruction;

import java.util.ArrayList;
import java.util.List;

/**
 * @since 1/27/2019
 */
public class IfStatement implements BlockStatement {

    private Expression condition;

    public IfStatement() {}

    private IfStatement(Expression condition) {
        this.condition = condition;
    }

    @Override
    public Statement parseHeader(TokenLine tokens, Parser parser) {
        if (tokens.size() == 1) {
            throw ErrorLog.get("Expected expression after 'if'");
        }
        return new IfStatement(parser.parseFrom(tokens, 1));
    }

    @Override
    public List<Instruction> build(Clause base, List<Clause> optional) {
        // todo assert that if there is an 'else' clause, it occurs only once and at the end
        List<Clause> clauses = joined(base, optional);
        int remaining = computeCompiledSize(clauses);
        List<Instruction> compiled = new ArrayList<>(remaining);
        for (Clause clause : clauses) {
            remaining -= clause.BODY.size() + 2;
            if (clause.NAME.equals("else")) {
                compiled.addAll(clause.BODY);
            } else {
                int offset = remaining > 0 ? 2 : 1;
                Statement header = clause.HEADER;
                Expression condition = header instanceof IfStatement
                        ? ((IfStatement) header).condition
                        : ((ElseIfClause) header).getCondition();
                compiled.add(new JumpUnlessInstruction(clause.BODY.size() + offset, condition));
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
        return name.equals("else") || name.equals("else if");
    }

    @Override
    public String getName() {
        return "if";
    }

    private static List<Clause> joined(Clause base, List<Clause> optional) {
        List<Clause> clauses = new ArrayList<>(optional);
        clauses.add(0, base);
        return clauses;
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
