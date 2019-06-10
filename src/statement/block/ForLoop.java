package statement.block;

import expression.Expression;
import expression.StackValue;
import expression.operation.UnaryOperatorCall;
import instruction.*;
import parse.Parser;
import parse.Token.TokenType;
import parse.TokenLine;
import parse.error.ErrorLog;
import statement.Statement;
import statement.block.clause.ClauseData;

import java.util.List;

import static parse.error.ErrorDescription.BAD_FOR_LOOP;

/**
 * @since 1/19/2019
 */
public class ForLoop implements BlockStatementType {

    @Override
    public Statement parseHeader(TokenLine tokens, Parser parser) {
        if (tokens.size() == 1 || tokens.get(1).TYPE != TokenType.IDENTIFIER) {
            throw ErrorLog.raise(BAD_FOR_LOOP, "Expected variable name after 'for'");
        } else if (tokens.size() == 2 || !tokens.get(2).matches("in")) {
            throw ErrorLog.raise(BAD_FOR_LOOP, "Expected 'in' after variable '%s'", tokens.get(1));
        } else if (tokens.size() == 3) {
            throw ErrorLog.raise(BAD_FOR_LOOP, "Expected loop expression after 'in'");
        }
        return new Statement(this, parser.parseFrom(tokens, 3), tokens.get(1).VALUE);
    }

    @Override
    public List<Instruction> build(List<ClauseData> clauses) {
        String variable = clauses.get(0).HEADER.STRING;
        Expression iterable = clauses.get(0).HEADER.EXPRESSION;
        List<Instruction> body = clauses.get(0).BODY;
        return asList(body.size() + 5,
                new PushStackInstruction(new UnaryOperatorCall(iterable, "_loop")),
                new JumpUnlessInstruction(body.size() + 3, new UnaryOperatorCall(new StackValue(), "has_next")),
                new AssignInstruction(variable, new UnaryOperatorCall(new StackValue(), "next")),
                body,
                new JumpInstruction(-body.size() - 2),
                new PopStackInstruction());
    }

    @Override
    public String getKeyword() {
        return "for";
    }

}
