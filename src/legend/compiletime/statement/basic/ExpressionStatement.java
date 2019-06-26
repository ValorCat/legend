package legend.compiletime.statement.basic;

import legend.compiletime.Parser;
import legend.compiletime.TokenLine;
import legend.compiletime.statement.Statement;
import legend.runtime.instruction.EvaluateInstruction;
import legend.runtime.instruction.Instruction;

import java.util.List;

public class ExpressionStatement implements BasicStatementType {

    @Override
    public boolean matches(TokenLine tokens) {
        return true;
    }

    @Override
    public Statement parse(TokenLine tokens, Parser parser) {
        return new Statement(this, parser.parseExpression(tokens));
    }

    @Override
    public List<Instruction> build(Statement stmt) {
         return List.of(new EvaluateInstruction(stmt.EXPRESSION));
    }

    @Override
    public String getName() {
        return "expr";
    }
}
