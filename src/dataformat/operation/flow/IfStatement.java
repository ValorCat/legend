package dataformat.operation.flow;

import dataformat.Expression;
import dataformat.operation.Operation;
import dataformat.value.LBoolean;
import dataformat.value.LNull;
import dataformat.value.Value;
import execute.Environment;
import parse.Token;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

/**
 * @since 1/27/2019
 */
public class IfStatement extends Operation implements FlowController {

    private int startAddress, endAddress;
    private Map<Expression, Integer> branches;
    private boolean done;

    public IfStatement(int position, List<Token> tokens, int address, Stack<FlowController> controlStack) {
        super(tokens.get(position));
        startAddress = address;
        parse(position, tokens);
        controlStack.push(this);
    }

    @Override
    protected void parse(int pos, List<Token> tokens) {
        branches = new LinkedHashMap<>();
        parseIf(pos, tokens);
    }

    @Override
    public Value evaluate(Environment env) {
        if (done) {
            env.setCounter(endAddress);
        } else {
            env.getControlStack().push(this);
            for (Entry<Expression, Integer> branch : branches.entrySet()) {
                if (branch.getKey().evaluate(env).asBoolean()) {
                    env.setCounter(branch.getValue() + 1);
                    done = true;
                    break;
                }
            }
            if (!done) {
                env.setCounter(endAddress);
            }
        }
        return LNull.NULL;
    }

    @Override
    public boolean isDone(Environment env) {
        return true;
    }

    @Override
    public void setJumpPoint(int address, int tokenPos, List<Token> statement) {
        switch (statement.get(tokenPos).VALUE) {
            case "end":
                endAddress = address;
                break;
            case "elsif":
                parseElsif(tokenPos, statement, address);
                break;
            case "else":
                parseElse(tokenPos, statement, address);
                break;
            default:
                throw new RuntimeException("Unexpected symbol '" + statement.get(0).VALUE + "'");
        }
    }

    private void parseIf(int pos, List<Token> statement) {
        if (pos > 0) {
            throw new RuntimeException("Unexpected symbol 'if'");
        } else if (statement.size() == 1 || !statement.get(1).isValue()) {
            throw new RuntimeException("Expected boolean expression after 'if'");
        } else if (statement.size() > 2) {
            throw new RuntimeException("Unexpected symbol '" + statement.get(2) + "'");
        }
        branches.put(statement.get(1).asExpression(), startAddress);
        Token.consolidate(statement, Token.newStatement("if", this), 0, 2);
    }

    private void parseElsif(int pos, List<Token> statement, int address) {
        if (pos > 0) {
            throw new RuntimeException("Unexpected symbol 'elsif'");
        } else if (statement.size() == 1 || !statement.get(1).isValue()) {
            throw new RuntimeException("Expected boolean expression after 'elsif'");
        } else if (statement.size() > 2) {
            throw new RuntimeException("Unexpected symbol '" + statement.get(2) + "'");
        }
        branches.putIfAbsent(statement.get(1).asExpression(), address);
        Token.consolidate(statement, Token.newStatement("elsif", this), 0, 2);
    }

    private void parseElse(int pos, List<Token> statement, int address) {
        if (pos > 0) {
            throw new RuntimeException("Unexpected symbol 'else'");
        } else if (statement.size() > 1) {
            throw new RuntimeException("Unxpected symbol '" + statement.get(1).VALUE + "'");
        }
        branches.putIfAbsent(LBoolean.TRUE, address);
        Token.consolidate(statement, Token.newStatement("else", this), 0, 1);
    }

}
