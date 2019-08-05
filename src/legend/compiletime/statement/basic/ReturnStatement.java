package legend.compiletime.statement.basic;

import legend.compiletime.Parser;
import legend.compiletime.TokenLine;
import legend.compiletime.expression.Expression;
import legend.compiletime.statement.Statement;
import legend.runtime.instruction.Instruction;
import legend.runtime.instruction.ReturnInstruction;

import java.util.List;

/**
 * @since 2/16/2019
 */
public class ReturnStatement implements BasicStatement {

    private Expression value;

    public ReturnStatement() {}

    private ReturnStatement(Expression value) {
        this.value = value;
    }

    @Override
    public Statement parse(TokenLine tokens, Parser parser) {
        if (tokens.size() == 1) {
            return new ReturnStatement();
        } else {
            return new ReturnStatement(parser.parseFrom(tokens, 1));
        }
    }

    @Override
    public List<Instruction> build() {
        return List.of(new ReturnInstruction(value));
    }

    @Override
    public String getName() {
        return "return";
    }

}
