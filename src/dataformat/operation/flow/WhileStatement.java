package dataformat.operation.flow;

import dataformat.Expression;
import dataformat.operation.Operation;
import dataformat.value.IntValue;
import dataformat.value.Value;
import execute.Environment;
import parse.Token;

import java.util.List;

/**
 * @since 1/20/2019
 */
public class WhileStatement extends Operation implements FlowController {

    private int startIndex, endIndex;
    private Expression condition;

    public WhileStatement(int position, List<Token> tokens) {
        super(position, tokens);
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
        Token.consolidate(tokens, new Token("while", this), 0, 2);
    }

    @Override
    public Value evaluate(Environment env) {
        if (checkCondition(env)) {
            env.getControlStack().push(this);
            startIndex = env.getCounter();
        } else {
            env.setCounter(endIndex + 1);
        }
        return new IntValue(0);
    }

    @Override
    public boolean isDone(Environment env) {
        if (checkCondition(env)) {
            env.setCounter(startIndex + 1);
            return false;
        }
        return true;
    }

    @Override
    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }

    private boolean checkCondition(Environment env) {
        Value result = condition.evaluate(env);
        if (!result.isType("bool")) {
            throw new RuntimeException("While loop expected boolean condition, got type '"
                    + result.type().getName() + "'");
        }
        return result.asBool();
    }

}
