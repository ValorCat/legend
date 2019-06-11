package compiletime.statement.block;

import compiletime.Parser;
import compiletime.Token.TokenType;
import compiletime.TokenLine;
import compiletime.error.ErrorLog;
import compiletime.expression.Expression;
import compiletime.expression.StackValue;
import compiletime.expression.operation.UnaryOperatorCall;
import compiletime.statement.Statement;
import compiletime.statement.block.clause.Clause;
import runtime.instruction.*;

import java.util.List;

/**
 * @since 1/19/2019
 */
public class ForLoop implements BlockStatementType {

    @Override
    public Statement parseHeader(TokenLine tokens, Parser parser) {
        if (tokens.size() == 1 || tokens.get(1).TYPE != TokenType.IDENTIFIER) {
            throw ErrorLog.get("Expected variable name after 'for'");
        } else if (tokens.size() == 2 || !tokens.get(2).matches("in")) {
            throw ErrorLog.get("Expected 'in' after variable '%s'", tokens.get(1));
        } else if (tokens.size() == 3) {
            throw ErrorLog.get("Expected loop expression after 'in'");
        }
        return new Statement(this, parser.parseFrom(tokens, 3), tokens.get(1).VALUE);
    }

    @Override
    public List<Instruction> build(Clause base, List<Clause> optional) {
        String variable = base.HEADER.STRING;
        Expression iterable = base.HEADER.EXPRESSION;
        List<Instruction> body = base.BODY;
        return asList(body.size() + 5,
                new PushStackInstruction(new UnaryOperatorCall(iterable, "_loop")),
                new JumpUnlessInstruction(body.size() + 3, new UnaryOperatorCall(new StackValue(), "has_next")),
                new AssignInstruction(variable, new UnaryOperatorCall(new StackValue(), "next")),
                body,
                new JumpInstruction(-body.size() - 2),
                new PopStackInstruction());
    }

    @Override
    public String getName() {
        return "for";
    }

}
