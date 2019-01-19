package dataformat.operation;

import dataformat.ArgumentList;
import dataformat.value.Value;
import execute.Environment;
import execute.StandardLibrary;
import parse.Token;

import java.util.List;

/**
 * @since 1/19/2019
 */
public class ToOperation extends Operation {

    public ToOperation(int position, List<Token> tokens) {
        super(position, tokens);
    }

    @Override
    public Value evaluate(Environment env) {
        Value left = operands.get(0).evaluate(env);
        Value right = operands.get(1).evaluate(env);
        if (!left.isType("int") || !right.isType("int")) {
            throw new RuntimeException("Operator 'to' expected (int, int) but got (" + left.type() + ", "
                    + right.type() + "' instead");
        }
        return StandardLibrary.type("range").instantiate(new ArgumentList(left, right), env);
    }
}
