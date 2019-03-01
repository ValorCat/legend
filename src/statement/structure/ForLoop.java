package statement.structure;

import execute.Environment;
import expression.Expression;
import expression.value.Value;
import parse.ErrorLog;
import parse.Parser;
import parse.Token;
import parse.Token.TokenType;

import java.util.List;
import java.util.Optional;

import static parse.ErrorDescription.BAD_FOR_LOOP;

/**
 * @since 1/24/2019
 */
public class ForLoop implements FlowController {

    private int startAddress, endAddress;
    private Expression iterable;
    private Value iterator;
    private String variable;

    public ForLoop(List<Token> tokens, Parser parser) {
        if (tokens.size() == 1 || tokens.get(1).TYPE != TokenType.IDENTIFIER) {
            throw ErrorLog.raise(BAD_FOR_LOOP, "Expected variable name after 'for'");
        } else if (tokens.size() == 2 || !tokens.get(2).matches("in")) {
            throw ErrorLog.raise(BAD_FOR_LOOP, "Expected 'in' after variable '%s'", tokens.get(1));
        } else if (tokens.size() == 3 || !tokens.get(3).isValue()) {
            throw ErrorLog.raise(BAD_FOR_LOOP, "Expected loop expression after 'in'");
        }
        variable = tokens.get(1).VALUE;
        iterable = parser.parseFrom(tokens, 3);
    }

    @Override
    public void execute(Environment env) {
        iterator = getIterator(iterable.evaluate(env), env);
        if (hasNext(env)) {
            env.getControlStack().push(this);
            startAddress = env.getCounter();
            env.assignLocal(variable, getNext(env));
        } else {
            env.setCounter(endAddress + 1);
        }
    }

    @Override
    public boolean isDone(Environment env) {
        if (hasNext(env)) {
            env.assignLocal(variable, getNext(env));
            env.setCounter(startAddress + 1);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void setJumpPoint(List<Token> tokens, Parser parser) {
        if (tokens.get(0).matches("end")) {
            this.endAddress = parser.getAddress();
        } else {
            FlowController.invalidJumpPoint(tokens.get(0));
        }
    }

    public String toString() {
        return "for(" + variable + " " + iterable + ")";
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
