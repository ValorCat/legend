package statement.block;

import expression.Expression;
import instruction.Instruction;
import instruction.JumpInstruction;
import instruction.JumpUnlessInstruction;
import parse.Parser;
import parse.TokenLine;
import parse.error.ErrorLog;
import statement.StatementData;
import statement.block.clause.ClauseData;

import java.util.List;

import static parse.error.ErrorDescription.BAD_WHILE_LOOP;

/**
 * @since 1/19/2019
 */
public class WhileLoop implements BlockStatementType {

    @Override
    public StatementData parseHeader(TokenLine tokens, Parser parser) {
        if (tokens.size() == 1) {
            throw ErrorLog.raise(BAD_WHILE_LOOP, "Expected expression after 'while'");
        }
        return new StatementData(this, parser.parseFrom(tokens, 1));
    }

    @Override
    public List<Instruction> build(List<ClauseData> clauses) {
        Expression condition = clauses.get(0).HEADER.EXPRESSION;
        List<Instruction> body = clauses.get(0).BODY;
        return asList(body.size() + 2,
                new JumpUnlessInstruction(body.size() + 2, condition),
                body,
                new JumpInstruction(-body.size() - 1));
    }

    @Override
    public String getKeyword() {
        return "while";
    }

}
