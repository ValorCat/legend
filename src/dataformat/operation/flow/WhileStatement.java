package dataformat.operation.flow;

import dataformat.Expression;
import dataformat.operation.Operation;
import dataformat.value.LNull;
import dataformat.value.Value;
import execute.Environment;
import parse.Token;

import java.util.List;
import java.util.Stack;

/**
 * @since 1/20/2019
 */
public class WhileStatement extends Operation implements FlowController {

    private int startAddress, endAddress;
    private Expression condition;

    public WhileStatement(int position, List<Token> tokens, Stack<FlowController> controlStack) {
        super(position, tokens);
        controlStack.push(this);
    }

    @Override
    protected void parse(int pos, List<Token> tokens) {
        if (pos > 0) {
            throw new RuntimeException("Unexpected symbol 'while'");
        } else if (tokens.size() == 1 || !tokens.get(1).isValue()) {
            throw new RuntimeException("Expected boolean condition after 'while'");
        } else if (tokens.size() > 2) {
            throw new RuntimeException("Unexpected symbol '" + tokens.get(2) + "'");
        }
        condition = tokens.get(1).asExpression();
        Token.consolidate(tokens, Token.newStatement("while", this), 0, 2);
    }

    @Override
    public Value evaluate(Environment env) {
        if (checkCondition(env)) {
            env.getControlStack().push(this);
            startAddress = env.getCounter();
        } else {
            env.setCounter(endAddress + 1);
        }
        return LNull.NULL;
    }

    @Override
    public boolean isDone(Environment env) {
        if (checkCondition(env)) {
            env.setCounter(startAddress + 1);
            return false;
        }
        return true;
    }

    @Override
    public void setJumpPoint(int address, int tokenPos, List<Token> statement) {
        if (statement.get(tokenPos).matches("end")) {
            this.endAddress = address;
        } else {
            throw new RuntimeException("Unexpected symbol '" + statement.get(0).VALUE + "'");
        }
    }

    @Override
    public String toString() {
        return "while(" + condition + ")";
    }

    private boolean checkCondition(Environment env) {
        Value result = condition.evaluate(env);
        if (!result.isType("Boolean")) {
            throw new RuntimeException("While loop expected boolean condition, got type '"
                    + result.type().getName() + "'");
        }
        return result.asBoolean();
    }

}
