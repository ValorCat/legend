package statement.basic;

import instruction.EvaluateInstruction;
import instruction.Instruction;
import parse.Parser;
import parse.TokenLine;
import statement.StatementData;

import java.util.List;

public class ExpressionStatement implements BasicStatement {

    @Override
    public boolean matches(TokenLine tokens) {
        return true;
    }

    @Override
    public StatementData parse(TokenLine tokens, Parser parser) {
        return new StatementData(parser.parseFrom(tokens, 0));
    }

    @Override
    public List<Instruction> build(StatementData data) {
         return List.of(new EvaluateInstruction(data.EXPRESSION));
    }

    @Override
    public String getKeyword() {
        return "<expr>";
    }
}
