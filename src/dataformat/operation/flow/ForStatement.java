package dataformat.operation.flow;

import dataformat.Expression;
import dataformat.operation.Operation;
import dataformat.value.LNull;
import dataformat.value.Value;
import execute.Environment;
import parse.Token;
import parse.Token.TokenType;

import java.util.List;
import java.util.Optional;
import java.util.Stack;

/**
 * @since 1/24/2019
 */
public class ForStatement extends Operation implements FlowController {

    private int startAddress, endAddress;
    private Expression iterable;
    private Value iterator;
    private String variable;

    public ForStatement(int position, List<Token> tokens, Stack<FlowController> controlStack) {
        super(position, tokens);
        controlStack.push(this);
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
        variable = tokens.get(1).VALUE;
        iterable = tokens.get(3).asExpression();
        Token.consolidate(tokens, new Token("for", this), 0, 4);
    }

    @Override
    public Value evaluate(Environment env) {
        iterator = getIterator(iterable.evaluate(env), env);
        if (hasNext(env)) {
            env.getControlStack().push(this);
            startAddress = env.getCounter();
            env.assign(variable, getNext(env));
        } else {
            env.setCounter(endAddress + 1);
        }
        return LNull.NULL;
    }

    @Override
    public boolean isDone(Environment env) {
        if (hasNext(env)) {
            env.assign(variable, getNext(env));
            env.setCounter(startAddress + 1);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void setJumpPoint(int address, int tokenPos, List<Token> statement) {
        if (statement.get(tokenPos).matches("end")) {
            this.endAddress = address;
        } else {
            throw new RuntimeException("Unexpected symbol '" + statement.get(0).VALUE + "'");
        }
    }

    private boolean hasNext(Environment env) {
        return iterator.callMethod("has_next", env).asBoolean();
    }

    private Value getNext(Environment env) {
        return iterator.callMethod("next", env);
    }

    private static Value getIterator(Value iterable, Environment env) {
        Optional<Value> maybeIterator = iterable.getOptionalAttribute("iterator");
        if (!maybeIterator.isPresent()) {
            throw new RuntimeException("Expected for loop target to have 'iterator' method, got value of type '"
                    + iterable.type().getName() + "' instead");
        }
        Value iterator = iterable.callMethod("iterator", env);
        if (!iterator.isType("Iterator")) {
            throw new RuntimeException("Expected for loop target's 'iterator' method to return an iterator, got "
                    + "value of type '" + iterator.type().getName() + "' instead");
        }
        return iterator;
    }

}
