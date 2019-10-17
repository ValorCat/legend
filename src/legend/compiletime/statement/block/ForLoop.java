package legend.compiletime.statement.block;

import legend.compiletime.Parser;
import legend.compiletime.TokenLine;
import legend.compiletime.error.ErrorLog;
import legend.compiletime.expression.Expression;
import legend.compiletime.expression.value.BoolValue;
import legend.compiletime.expression.value.NullValue;
import legend.compiletime.statement.Statement;
import legend.compiletime.statement.block.clause.Clause;
import legend.runtime.instruction.*;

import java.util.List;

import static legend.compiletime.expression.TopOfStack.TOP_OF_STACK;

/**
 * @since 1/19/2019
 */
public class ForLoop implements BlockStatement {

    private String variable;
    private Expression variableType;
    private Expression iterable;

    public ForLoop() {}

    private ForLoop(String variable, Expression variableType, Expression iterable) {
        this.variable = variable;
        this.variableType = variableType;
        this.iterable = iterable;
    }

    @Override
    public Statement parseHeader(TokenLine tokens, Parser parser) {
        int inPos = tokens.indexOf("in");
        if (tokens.size() == 1 || inPos == 1) {
            throw ErrorLog.get("Expected loop variable after 'for'");
        } else if (inPos == -1) {
            throw ErrorLog.get("Expected 'in' in loop header");
        } else if (inPos == tokens.size() - 1) {
            throw ErrorLog.get("Expected loop expression after 'in'");
        }
        String iterator = tokens.get(inPos - 1).VALUE;
        Expression type = null;
        Expression iterable = parser.parseFrom(tokens, inPos + 1);
        if (inPos > 2) {
            type = parser.parseBetween(tokens, 1, inPos - 2);
            if (!type.isCompact()) {
                throw ErrorLog.get("Loop variable type expression must be wrapped in parentheses");
            }
        }
        return new ForLoop(iterator, type, iterable);
    }

    @Override
    public List<Instruction> build(Clause base, List<Clause> optional) {
        ForLoop header = (ForLoop) base.HEADER;
        String variable = header.variable;
        Expression variableType = header.variableType;
        Expression iterable = header.iterable;
        List<Instruction> body = base.BODY;

        Expression getIterator = scope -> iterable.evaluate(scope).operateUnary("for");
        Expression getNext = scope -> TOP_OF_STACK.evaluate(scope).operateUnary("next");
        Expression hasNext = scope -> BoolValue.resolve(TOP_OF_STACK.evaluate(scope) != NullValue.NULL);

        return asList(body.size() + 5,
                new PushStackInstruction(getIterator),
                new PushStackInstruction(getNext),
                new JumpUnlessInstruction(body.size() + 4, hasNext),
                variableType == null
                        ? new AssignUntypedInstruction(variable, TOP_OF_STACK)
                        : new AssignTypedInstruction(variable, variableType, TOP_OF_STACK),
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
