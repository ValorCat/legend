package compiletime.statement.basic;

import compiletime.Parser;
import compiletime.TokenLine;
import compiletime.statement.Statement;
import runtime.instruction.EvaluateInstruction;
import runtime.instruction.Instruction;

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
        return "<expr>";
    }
}
