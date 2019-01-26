package dataformat.operation.flow;

import dataformat.Expression;
import dataformat.operation.Operation;
import dataformat.value.IntValue;
import dataformat.value.Value;
import execute.Environment;
import parse.Token;
import parse.Token.TokenType;

import java.util.List;

/**
 * @since 1/24/2019
 */
public class ForStatement extends Operation implements FlowController {

    private int startIndex, endIndex;
    private String iterVar;
    private int iterValue, iterEnd;
    private Expression iterExpr;

    public ForStatement(int position, List<Token> tokens) {
        super(position, tokens);
    }

    @Override
    protected void parse(int pos, List<Token> tokens) {
        if (pos > 0) {
            throw new RuntimeException("Unexpected symbol 'for'");
        } else if (tokens.size() == 1 || tokens.get(1).TYPE != TokenType.IDENTIFIER) {
            throw new RuntimeException("Expected variable name after 'for'");
        } else if (tokens.size() == 2 || !tokens.get(2).matches("in")) {
            throw new RuntimeException("Expected 'in' after variable '" + tokens.get(1).VALUE + "'");
        } else if (tokens.size() != 4 || !tokens.get(3).isValue()) {
            throw new RuntimeException("Expected loop expression after 'in'");
        }
        iterVar = tokens.get(1).VALUE;
        iterExpr = tokens.get(3).asExpression();
        Token.consolidate(tokens, new Token("for", this), 0, 4);
    }

    @Override
    public Value evaluate(Environment env) {
        Value iterable = iterExpr.evaluate(env);
        if (!iterable.isType("range")) {
            throw new RuntimeException("Expected loop expression to be 'range', got '" + iterable.type().getName() + "'");
        }
        iterValue = iterable.getAttribute("left").asInt();
        iterEnd = iterable.getAttribute("right").asInt();
        if (iterValue <= iterEnd) {
            env.getControlStack().push(this);
            startIndex = env.getCounter();
            env.assign(iterVar, new IntValue(iterValue));
        } else {
            env.setCounter(endIndex + 1);
        }
        return new IntValue(0);
    }

    @Override
    public boolean isDone(Environment env) {
        iterValue++;
        if (iterValue <= iterEnd) {
            env.assign(iterVar, new IntValue(iterValue));
            env.setCounter(startIndex + 1);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }

}
