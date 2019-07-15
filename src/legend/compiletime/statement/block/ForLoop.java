package legend.compiletime.statement.block;

import legend.compiletime.Parser;
import legend.compiletime.Token.TokenType;
import legend.compiletime.TokenLine;
import legend.compiletime.error.ErrorLog;
import legend.compiletime.expression.Expression;
import legend.compiletime.expression.value.LBoolean;
import legend.compiletime.expression.value.LNull;
import legend.compiletime.statement.Statement;
import legend.compiletime.statement.block.clause.Clause;
import legend.runtime.instruction.*;

import java.util.List;

import static legend.compiletime.expression.TopOfStack.TOP_OF_STACK;

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

        Expression getIterator = scope -> iterable.evaluate(scope).operateUnary("for");
        Expression getNext = scope -> TOP_OF_STACK.evaluate(scope).operateUnary("next");
        Expression hasNext = scope -> LBoolean.resolve(TOP_OF_STACK.evaluate(scope) != LNull.NULL);

        return asList(body.size() + 5,
                new PushStackInstruction(getIterator),
                new PushStackInstruction(getNext),
                new JumpUnlessInstruction(body.size() + 4, hasNext),
                new AssignInstruction(variable, TOP_OF_STACK),
                new PopStackInstruction(),
                body,
                new JumpInstruction(-body.size() - 4),
                new PopStackInstruction(),
                new PopStackInstruction());
    }

    @Override
    public String getName() {
        return "for";
    }

}
