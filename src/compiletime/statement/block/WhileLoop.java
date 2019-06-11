package compiletime.statement.block;

import compiletime.Parser;
import compiletime.TokenLine;
import compiletime.error.ErrorLog;
import compiletime.expression.Expression;
import compiletime.statement.Statement;
import compiletime.statement.block.clause.Clause;
import runtime.instruction.Instruction;
import runtime.instruction.JumpInstruction;
import runtime.instruction.JumpUnlessInstruction;

import java.util.List;

/**
 * @since 1/19/2019
 */
public class WhileLoop implements BlockStatementType {

    @Override
    public Statement parseHeader(TokenLine tokens, Parser parser) {
        if (tokens.size() == 1) {
            throw ErrorLog.get("Expected expression after 'while'");
        }
        return new Statement(this, parser.parseFrom(tokens, 1));
    }

    @Override
    public List<Instruction> build(Clause base, List<Clause> optional) {
        Expression condition = base.HEADER.EXPRESSION;
        List<Instruction> body = base.BODY;
        return asList(body.size() + 2,
                new JumpUnlessInstruction(body.size() + 2, condition),
                body,
                new JumpInstruction(-body.size() - 1));
    }

    @Override
    public String getName() {
        return "while";
    }

}
