package legend.compiletime.statement.block;

import legend.compiletime.Parser;
import legend.compiletime.TokenLine;
import legend.compiletime.error.ErrorLog;
import legend.compiletime.expression.Expression;
import legend.compiletime.statement.Statement;
import legend.compiletime.statement.block.clause.Clause;
import legend.runtime.instruction.Instruction;
import legend.runtime.instruction.JumpInstruction;
import legend.runtime.instruction.JumpUnlessInstruction;

import java.util.List;

/**
 * @since 1/19/2019
 */
public class WhileLoop implements BlockStatement {

    private Expression condition;

    public WhileLoop() {}

    private WhileLoop(Expression condition) {
        this.condition = condition;
    }

    @Override
    public Statement parseHeader(TokenLine tokens, Parser parser) {
        if (tokens.size() == 1) {
            throw ErrorLog.get("Expected expression after 'while'");
        }
        return new WhileLoop(parser.parseFrom(tokens, 1));
    }

    @Override
    public List<Instruction> build(Clause base, List<Clause> optional) {
        Expression condition = ((WhileLoop) base.HEADER).condition;
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
