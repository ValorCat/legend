package compiletime.statement.basic;

import compiletime.Parser;
import compiletime.TokenLine;
import compiletime.statement.Statement;
import runtime.instruction.Instruction;
import runtime.instruction.ReturnInstruction;

import java.util.List;

/**
 * @since 2/16/2019
 */
public class ReturnStatement implements BasicStatementType {

    @Override
    public boolean matches(TokenLine tokens) {
        return tokens.get(0).matches("return");
    }

    @Override
    public Statement parse(TokenLine tokens, Parser parser) {
        if (tokens.size() == 1) {
            return new Statement(this);
        } else {
            return new Statement(this, parser.parseFrom(tokens, 1));
        }
    }

    @Override
    public List<Instruction> build(Statement data) {
        return List.of(new ReturnInstruction(data.EXPRESSION));
    }

    @Override
    public String getKeyword() {
        return "return";
    }
}
