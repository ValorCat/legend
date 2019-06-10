package statement.basic;

import instruction.EvaluateInstruction;
import instruction.Instruction;
import parse.Parser;
import parse.TokenLine;
import statement.Statement;

import java.util.List;

public class ExpressionStatement implements BasicStatementType {

    @Override
    public boolean matches(TokenLine tokens) {
        return true;
    }

    @Override
    public Statement parse(TokenLine tokens, Parser parser) {
        return new Statement(this, parser.parseFrom(tokens, 0));
    }

    @Override
    public List<Instruction> build(Statement data) {
         return List.of(new EvaluateInstruction(data.EXPRESSION));
    }

    @Override
    public String getKeyword() {
        return "<expr>";
    }
}
